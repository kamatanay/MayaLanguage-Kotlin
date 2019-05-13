package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.ParseTreeNode
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.plus
import org.junit.Assert
import org.junit.Test

class ExectorTest {

    val additionRule = RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("E")
    val additionResumeRule = Rule(Symbol.NonTerminal("AdditionResume"))
    val valueRule = RuleFor("E") with Symbol.Terminal("x")

    val numberTerminalParseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "1")
    val operatorTerminalParseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "+")
    val valueParseTreeNode = ParseTreeNode.NonTerminalNode(valueRule, listOf(numberTerminalParseTreeNode))
    val additionalParseTreeNode = ParseTreeNode.NonTerminalNode(additionRule, listOf(valueParseTreeNode, operatorTerminalParseTreeNode, valueParseTreeNode))

    val numberExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.first().let {
            ExecutionData(listOf(it)+registerStack, emptyList())
        }
    }

    val additionResumeExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->
        val (firstValue: ParseTreeNode.TerminalNode, nextStack) = registerStack.pop() as Pair<ParseTreeNode.TerminalNode, List<ParseTreeNode>>
        val (secondValue: ParseTreeNode.TerminalNode, finalStack) = nextStack.pop() as Pair<ParseTreeNode.TerminalNode, List<ParseTreeNode>>
        val value = firstValue.input.toInt() + secondValue.input.toInt()
        val responseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), value.toString())
        ExecutionData(listOf(responseTreeNode)+finalStack, emptyList())
    }

    val additionExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.let { inputs ->
            ExecutionData(registerStack, listOf(inputs.first(), inputs.last(), ParseTreeNode.NonTerminalNode(additionResumeRule, emptyList())))
        }
    }



    @Test
    fun itShouldFindExecutorForRuleInParseTreeNodeWithRegisterStackAndReturnNewParseTreeNodeWithRegisterStack(){

        val startNode:ExecutorData =
                Pair(additionalParseTreeNode, emptyList())

        val executor = Executor(mapOf(valueRule to numberExecutor, additionRule to additionExecutor, additionResumeRule to additionResumeExecutor))

        val executionData = executor.execute(startNode)

        Assert.assertEquals(ExecutionData(listOf(ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "2")), emptyList()), executionData)

    }

}