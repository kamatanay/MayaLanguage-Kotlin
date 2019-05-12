package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.ParseTreeNode
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.plus
import org.junit.Assert
import org.junit.Test

class ExectorTest {

    val additionRule = RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("E")
    val valueRule = RuleFor("E") with Symbol.Terminal("x")
    val assignmentRule = RuleFor("V") with Symbol.Terminal("v") + Symbol.Terminal("=") + Symbol.NonTerminal("E")

    val numberTerminalParseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "1")
    val operatorTerminalParseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "+")
    val variableTerminalParseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "sum")
    val assignmentOperatorParseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("="), "=")
    val valueParseTreeNode = ParseTreeNode.NonTerminalNode(valueRule, listOf(numberTerminalParseTreeNode))
    val additionalParseTreeNode = ParseTreeNode.NonTerminalNode(additionRule, listOf(valueParseTreeNode, operatorTerminalParseTreeNode, valueParseTreeNode))
    val assignmentParseTreeNode = ParseTreeNode.NonTerminalNode(assignmentRule, listOf(variableTerminalParseTreeNode, assignmentOperatorParseTreeNode, additionalParseTreeNode))

    val numberExecutor:ParseTreeExecutor = { executor, (treeNode, context, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.first().let {
            Pair(context, listOf(it)+registerStack)
        }
    }

    val numberTerminalExecutor:ParseTreeExecutor = { executor, (treeNode, context, registerStack) ->
        Pair(context, registerStack)
    }

    val additionExecutor:ParseTreeExecutor = { executor, (treeNode, context, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.let { inputs ->
            inputs.first().let {
                executor.execute(ExecutorData(it, context, registerStack))
            }.let { (context, registerStack) ->
                executor.execute(ExecutorData(inputs.last(), context, registerStack))
            }.let { (context, registerStack) ->
                val (firstValue: ParseTreeNode.TerminalNode, nextStack) = registerStack.pop() as Pair<ParseTreeNode.TerminalNode, List<ParseTreeNode>>
                val (secondValue: ParseTreeNode.TerminalNode, finalStack) = nextStack.pop() as Pair<ParseTreeNode.TerminalNode, List<ParseTreeNode>>
                val value = firstValue.input.toInt() + secondValue.input.toInt()
                val responseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), value.toString())
                ExecutionData(context, listOf(responseTreeNode)+finalStack)
            }
        }
    }

    val assignmentExecutor:ParseTreeExecutor = { executor, (treeNode, context, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.let { inputs ->
            inputs.last().let {
                executor.execute(ExecutorData(it, context, registerStack))
            }.let { (context, registerStack) ->
                val (value, nextStack) = registerStack.pop() as Pair<ParseTreeNode, List<ParseTreeNode>>
                val contextIdentifier = (inputs.first() as ParseTreeNode.TerminalNode).input
                ExecutionData(context + mapOf(contextIdentifier to value), nextStack)
            }
        }
    }


    @Test
    fun itShouldFindExecutorForRuleInParseTreeNodeWithContextMapAndRegisterStackAndReturnNewParseTreeNodeWithNewContextMapAndRegisterStack(){

        val startNode:ExecutorData =
                Triple(assignmentParseTreeNode, emptyMap(), emptyList())

        val executor = Executor(mapOf(valueRule to numberExecutor, assignmentRule to assignmentExecutor, additionRule to additionExecutor))

        val executionData = executor.execute(startNode)

        Assert.assertEquals(ExecutionData(mapOf("sum" to ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "2")), emptyList()), executionData)

    }

}