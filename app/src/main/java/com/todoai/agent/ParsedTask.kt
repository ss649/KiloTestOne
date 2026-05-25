package com.todoai.agent

/** The shape the Task Parser returns after every LLM call turn. */
data class ParsedTask(
    val title: String = "",
    val description: String = "",
    val priority: String = "MEDIUM",          // LOW | MEDIUM | HIGH
    val category: String = "General",
    val dueAtMillis: Long? = null,
    val needsClarification: Boolean = false,
    val clarificationQuestion: String = "",
)
