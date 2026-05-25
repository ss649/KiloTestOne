package com.todoai.agent.tools

import javax.inject.Inject

/**
 * PriorityAgent — keyword-based priority suggestion tool.
 * Takes a task title (and optional description) and returns a recommended priority level.
 * Operates entirely offline; no external API needed.
 */
class PriorityAgent @Inject constructor() : Tool {
    override val name = "PRIORITY_SUGGEST"
    override val description = "Suggests a priority level (LOW / MEDIUM / HIGH) for a task title."

    private val highKeywords = listOf("urgent", "asap", "deadline", "critical", "emergency", "important", "meeting")
    private val lowKeywords  = listOf("later", "someday", "whenever", "optional", "read", "watch")

    override suspend fun call(args: Map<String, String>): String {
        val title = (args["title"] ?: args["text"] ?: "").lowercase()
        return when {
            highKeywords.any { title.contains(it) } -> "HIGH"
            lowKeywords.any  { title.contains(it) } -> "LOW"
            else                                      -> "MEDIUM"
        }
    }
}
