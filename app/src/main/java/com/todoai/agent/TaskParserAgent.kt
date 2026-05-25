package com.todoai.agent

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Heuristic (deterministic) natural-language parser — works 100% offline with no external API.
 * Uses structured string patterns to extract title, priority, category, and due-date, then
 * returns a [ParsedTask]. The [TaskParserAgent] is the single entry point for all NLP-powered
 * task entry including voice input.
 *
 * Supported patterns:
 *   title [!high | !medium | !low]
 *   title #category
 *   title tomorrow / today / next monday / 5pm / may 25 / 2026-05-25
 *   Combined patterns are all supported simultaneously.
 */
class TaskParserAgent {

    private val priorityRegex = Regex("""!(\w+)""", RegexOption.IGNORE_CASE)
    private val categoryRegex = Regex("""#(\w+)""", RegexOption.IGNORE_CASE)
    private val dateRegex = Regex(
        pattern = """\b(today|tomorrow|next\s+\w+|in\s+\d+\s+\w+|on\s+\w+\s+\d+|\d{1,2}[/-]\d{1,2}([/-]\d{2,4})?|\d{1,2}:\d{2}\s*(am|pm)?)\b""",
        RegexOption.IGNORE_CASE
    )

    fun parse(input: String): ParsedTask {
        val lower = input.trim()
        val (cleanTitle, priority) = extractPriority(lower)
        val (withCategory, category) = extractCategory(cleanTitle)
        val (withDate, dueAt) = extractDate(withCategory)
        val title = withDate.trim().ifEmpty { input }

        return ParsedTask(
            title = title,
            priority = priority,
            category = category,
            dueAtMillis = dueAt,
        )
    }

    private fun extractPriority(text: String): Pair<String, String> {
        priorityRegex.find(text)?.let { m ->
            val val_ = m.groupValues[1].uppercase()
            if (val_ in setOf("LOW", "MEDIUM", "HIGH")) {
                val cleaned = text.replace(m.value, "").trim()
                return cleaned to val_
            }
        }
        return text to "MEDIUM"
    }

    private fun extractCategory(text: String): Pair<String, String> {
        categoryRegex.find(text)?.let { m ->
            val category = m.groupValues[1].replaceFirstChar { it.titlecase() }
            val cleaned = text.replace(m.value, "").trim()
            return cleaned to category
        }
        return text to "General"
    }

    private fun extractDate(text: String): Pair<String, Long?> {
        dateRegex.find(text)?.let { m ->
            val raw = m.value
            val millis = try {
                val now = System.currentTimeMillis()
                when (raw.lowercase()) {
                    "today" -> now
                    "tomorrow" -> now + 86_400_000L
                    else -> parseArbitrary(raw)
                }
            } catch (_: Exception) { null }
            val cleaned = text.replace(m.value, "").trim()
            return cleaned to millis
        }
        return text to null
    }

    private fun parseArbitrary(raw: String): Long? = try {
        val locale = Locale.getDefault()
        val today = Date()
        val day = SimpleDateFormat("d", locale)
        val month = SimpleDateFormat("M", locale)
        val year = SimpleDateFormat("yyyy", locale)
        val currentYear = year.format(today).toInt()

        // Try "MM/dd[,/YYYY]" first
        val slashDate = Regex("""(\d{1,2})[/-](\d{1,2})(?:[/-](\d{2,4}))?""")
        slashDate.find(raw)?.let { sm ->
            val m = sm.groupValues[1].toInt()
            val d = sm.groupValues[2].toInt()
            val y = sm.groupValues[3].toIntOrNull() ?: currentYear
            val fmt = SimpleDateFormat("MM/dd/yyyy", locale)
            return fmt.parse("$m/$d/${if (y < 100) 2000 + y else y}")?.time
        }

        // Try "d MMM" / "MMM d"
        val named = SimpleDateFormat("d MMM", locale)
        named.parse("$raw $currentYear")?.time
    } catch (_: Exception) { null }
}
