package com.todoai.agent.tools

import javax.inject.Inject

data class ScheduledTask(
    val title: String,
    val priority: String,
    val dueAtMillis: Long?,
    val suggestion: String
)

/**
 * SchedulerAgent — deadline + priority matching tool.
 * Takes a JSON blob of open tasks and returns a re-ordered schedule with a
 * plain-English rationale for each placement.
 */
class SchedulerAgent @Inject constructor(): Tool {
    override val name = "SCHEDULER"
    override val description = "Returns a recommended schedule order for open tasks."
    override suspend fun call(args: Map<String, String>): String {
        val raw = args["tasksJson"] ?: return "[]"

        val tasks = raw.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                val parts = line.split("|")

                ScheduledTask(
                    title = parts.getOrElse(0) { "" },
                    priority = parts.getOrElse(1) { "LOW" },
                    dueAtMillis = parts.getOrNull(2)?.toLongOrNull(),
                    suggestion = "Scheduled based on priority and deadline"
                )
            }

        return tasks
            .sortedWith(
                compareByDescending<ScheduledTask> { it.priority == "HIGH" }
                    .thenBy { it.dueAtMillis ?: Long.MAX_VALUE }
            )
            .joinToString("\n") { t ->
                "${t.priority.padEnd(6)} | ${t.title.padEnd(30)} | ${t.suggestion}"
            }
    }
}
