package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.ParseTreeNode
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.plus
import org.junit.Assert
import org.junit.Test

class ExectorLambdaCalculusTest {


    val variableExpressionRule = RuleFor("E") with Symbol.NonTerminal("V")
    val lambdaDefinitionRule =
        RuleFor("E") with Symbol.Terminal("(") + Symbol.Terminal("!") + Symbol.NonTerminal("V") + Symbol.Terminal(".") + Symbol.NonTerminal(
            "E"
        ) + Symbol.Terminal(")")
    val applicationRule =
        RuleFor("E") with Symbol.Terminal("(") + Symbol.NonTerminal("E") + Symbol.NonTerminal("E") + Symbol.Terminal(")")
    val terminalValueRule = RuleFor("V") with Symbol.Terminal("x")

    val applicationFinaliseRule = Rule(Symbol.NonTerminal("ResumeApplication"))

    val lambdaCalculusGrammer:Grammer = Grammer(listOf(
        variableExpressionRule,
        lambdaDefinitionRule,
        applicationRule,
        terminalValueRule

    ))

    val applicationFinaliseExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->

        val (function, registerAfterFunction) = registerStack.pop() as Pair<ParseTreeNode, List<ParseTreeNode>>
        val (parameter, registerAfterParameter) = registerAfterFunction.pop() as Pair<ParseTreeNode, List<ParseTreeNode>>

        when(function){
            is ParseTreeNode.NonTerminalNode -> {
                val parameterName = function.inputs.get(2).let { it as ParseTreeNode.NonTerminalNode }.let { it.inputs.first() as ParseTreeNode.TerminalNode }.let { it.input }
                function.inputs.get(4).let {
                    val node = it.let { it as ParseTreeNode.NonTerminalNode }.let { it.copy(context = it.context + mapOf(parameterName to parameter) + function.context) }
                    ExecutionData(registerAfterParameter, listOf(node))
                }
            }
            is ParseTreeNode.TerminalNode -> {
                when(function.input){
                    "print" -> {
                        print(parameter.let { it as ParseTreeNode.TerminalNode }.let { it.input })
                        ExecutionData(registerAfterParameter, emptyList())
                    }
                    "increment" -> {
                        val newValue = parameter.let { it as ParseTreeNode.TerminalNode }.let { it.input.toInt() + 1 }.let { it.toString() }
                        val newTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), newValue)
                        ExecutionData(listOf(newTreeNode)+registerAfterParameter, emptyList())
                    }
                    else -> ExecutionData(registerAfterParameter, listOf(function))
                }

            }
            else -> ExecutionData(registerAfterParameter, listOf(function))
        }
    }

    val applicationRuleExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.let { inputs ->
            val functionInput = inputs.get(1).applyContext(treeNode)
            val parameterInput = inputs.get(2).applyContext(treeNode)
            ExecutionData(registerStack, listOf(parameterInput, functionInput, ParseTreeNode.NonTerminalNode(applicationFinaliseRule, emptyList())))
        }
    }

    val lambdaDefinitionRuleExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->
        ExecutionData(listOf(treeNode)+registerStack, emptyList())
    }

    val terminalValueExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.first().let {

            val terminalValueNode = it as ParseTreeNode.TerminalNode
            val terminalName = terminalValueNode.input

            if (treeNode.context.containsKey(terminalName)){
                ExecutionData(listOf(treeNode.context.get(terminalName) as ParseTreeNode) + registerStack, emptyList())
            } else {
                ExecutionData(listOf(it)+registerStack, emptyList())
            }

        }
    }

    val variableExpressionExecutor:ParseTreeExecutor = { executor, (treeNode, registerStack) ->
        (treeNode as ParseTreeNode.NonTerminalNode).inputs.first().let {
            ExecutionData(registerStack, listOf(it.applyContext(treeNode)))
        }
    }


    @Test
    fun finalTest(){
        val tokenizerMap = listOf(
            Symbol.Terminal("(") to """^(\()""".toRegex(),
            Symbol.Terminal(")") to """^(\))""".toRegex(),
            Symbol.Terminal(".") to """^(\.)""".toRegex(),
            Symbol.Terminal("!") to """^(!)""".toRegex(),
            Symbol.Terminal("x") to """^([a-zA-Z0-9]+)""".toRegex(),
            Symbol.Terminal("x") to """^\"(.+)\"""".toRegex()
        )
        val tokenizer = Tokenizer(tokenizerMap)

        val zero = "(!f.(!x.x))"
        val once = "(!f.(!x.(f x)))"
        val inc = "(!n.(!f.(!x. (f ((n f)x) ) )))"
        val twice = "($inc $once)"
        val thrice = "($inc $twice)"

        val sum = "(!a.(!b.(!f.(!x. ((a f) ((b f) x))  ))))"
        val multiply = "(!a.(!b.(!f.(!x. ((a(b f))x) ))))"

        val multiplication = "((${multiply} $thrice)$thrice)"
        val adding = "((${sum} $thrice)$zero)"

        val inputStack = tokenizer.tokenize("((${adding} increment) 0 )")

        val finalStack = lambdaCalculusGrammer.initialSet().let {
            listOf(it)
        }.actionMap(lambdaCalculusGrammer, ParseTable()).let { ParseStack(it) }.build(ParseStack::process)(inputStack)

        val executor = Executor(mapOf(variableExpressionRule to variableExpressionExecutor, lambdaDefinitionRule to lambdaDefinitionRuleExecutor, applicationRule to applicationRuleExecutor, terminalValueRule to terminalValueExecutor, applicationFinaliseRule to applicationFinaliseExecutor))

        val executionData = executor.execute(ExecutorData(finalStack.top().parseTreeNode, emptyList()))

        Assert.assertEquals(ExecutionData(listOf(ParseTreeNode.TerminalNode(Symbol.Terminal("x"),"3")), emptyList()), executionData)

    }


}