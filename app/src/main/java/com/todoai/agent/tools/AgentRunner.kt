package com.todoai.agent.tools

/** Minimal Tool-Call/Observation loop — no external LLM required. */
interface Tool {
    val name: String
    val description: String
    suspend fun call(args: Map<String, String>): String
}

data class ToolCall(val toolName: String, val args: Map<String, String>)
data class Observation(val toolName: String, val result: String)

/** Stub runner — swap with real LLM call later. */
class AgentRunner(private val tools: List<Tool>) {

    suspend fun runTurn(input: String): List<Observation> {
        val call = ToolCall(toolName = "TODO_PARSER", args = mapOf("text" to input))
        val tool = tools.find { it.name == call.toolName } ?: return emptyList()
        val result = tool.call(call.args)
        return listOf(Observation(tool.name, result))
    }
}
