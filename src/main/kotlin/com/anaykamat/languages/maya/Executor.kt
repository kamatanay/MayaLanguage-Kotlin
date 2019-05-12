package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.ParseTreeNode
import com.anaykamat.languages.maya.models.Symbol

typealias RegisterStack = List<ParseTreeNode>
typealias ExecutorContext = Map<String, ParseTreeNode>
typealias ExecutorData = Triple<ParseTreeNode, ExecutorContext, RegisterStack>
typealias ExecutionData = Pair<ExecutorContext, RegisterStack>
typealias ParseTreeExecutor = (Executor, ExecutorData) -> ExecutionData

class Executor(private val executorMap: Map<Rule, ParseTreeExecutor>) {
    fun execute(data:ExecutorData):ExecutionData{
        val node = data.first
        return when(node){
            is ParseTreeNode.NonTerminalNode -> (executorMap.get(node.rule) as ParseTreeExecutor).invoke(this, data)
            is ParseTreeNode.TerminalNode -> ExecutionData(data.second + mapOf(node.symbol.let { it as Symbol.Terminal }.let { it.name } to node.input) as ExecutorContext, data.third)
            else -> return ExecutionData(data.second, data.third)
        }
    }
}