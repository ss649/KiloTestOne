package com.todoai.agent.tools

import javax.inject.Inject

/**
 * SummaryAgent — insights from completed tasks tool.
 * Takes a JSON list of completed tasks (title, priority, completedAtMillis) and
 * returns a plain-language productivity summary.
 */
class SummaryAgent @Inject constructor() : Tool {
    override val name = "SUMMARY"
    override val description = "Returns a plain-English productivity summary from completed tasks."

    override suspend fun call(args: Map<String, String>): String {
        val lines = (args["completedJson"] ?: "").lines().filter { it.isNotBlank() }
        val total = lines.size
        val highs = lines.count { it.contains("HIGH") }

        return buildString {
            appendLine("📋 Productivity Summary")
            appendLine("Tasks completed this period: $total")
            if (highs > 0) appendLine("✅ High-priority tasks crushed: $highs")
            if (total >= 10) appendLine("🔥 Great momentum — keep it up!")
            else if (total >= 5) appendLine("👍 Solid progress. Ready for more?")
            else appendLine("🌱 Every step counts. One task at a time.")
        }
    }
}
