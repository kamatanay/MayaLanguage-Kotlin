package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.ParseTreeNode

typealias RegisterStack = List<ParseTreeNode>
typealias ExecutorData = Pair<ParseTreeNode, RegisterStack>
typealias ExecutionData = Pair<RegisterStack, List<ParseTreeNode>>
typealias ParseTreeExecutor = (Executor, ExecutorData) -> ExecutionData

fun ParseTreeNode.applyContext(parentContextNode:ParseTreeNode):ParseTreeNode = when(this){
    is ParseTreeNode.NonTerminalNode -> this.copy(context = this.context + parentContextNode.context)
    is ParseTreeNode.TerminalNode -> this.copy(context = this.context + parentContextNode.context)
    else -> this
}

class Executor(private val executorMap: Map<Rule, ParseTreeExecutor>) {
    fun execute(data:ExecutorData): ExecutionData {

        var (currentParseNode, currentRegisterStack) = data

        var executorDataStack:List<ParseTreeNode> = emptyList<ParseTreeNode>() + listOf(currentParseNode)

        while(executorDataStack.count() > 0){
            val executorData = executorDataStack.pop()
            if (executorData == null) break
            val (data, remainingStack) = executorData
            val executionData = when(data){
                is ParseTreeNode.NonTerminalNode -> (executorMap.get(data.rule) as ParseTreeExecutor).invoke(this, ExecutorData(data, currentRegisterStack))
                is ParseTreeNode.TerminalNode -> ExecutionData(currentRegisterStack, remainingStack)
                else -> ExecutionData(currentRegisterStack, remainingStack)
            }
            executorDataStack = executionData.second + remainingStack
            currentRegisterStack = executionData.first
        }

        return ExecutionData(currentRegisterStack, emptyList())
    }
}