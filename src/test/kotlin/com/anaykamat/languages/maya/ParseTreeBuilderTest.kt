package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.*
import org.junit.Assert
import org.junit.Test

class ParseTreeBuilderTest {

    private val firstRuleSet = listOf(RuleFor("E") with Symbol.Terminal("0"))
    private val secondRule = RuleFor("E") with Symbol.Terminal("1")
    private val secondRuleSet = listOf(secondRule)

    @Test
    fun topOfEmptyParseStackShouldBeStartRuleSetWithStartNode(){
        val parseStack = ParseStack(listOf(
            Transition(firstRuleSet),
            Transition(secondRuleSet)
        ))
        val top:ParseStackEntry = parseStack.top()
        Assert.assertEquals(ParseStackEntry(firstRuleSet, ParseTreeNode.StartNode), top)
    }

    @Test
    fun pushShouldPushTheGivenEntryOnTopOfTheStack(){
        val parseStack = ParseStack(listOf(
            Transition(firstRuleSet),
            Transition(secondRuleSet)
        ))

        val newEntry = ParseStackEntry(secondRuleSet, ParseTreeNode.TerminalNode(Symbol.Terminal("1"), "1"))
        val pushedParseStack: ParseStack = parseStack.push(newEntry)
        val top:ParseStackEntry = pushedParseStack.top()
        Assert.assertEquals(newEntry, top)
    }

    @Test
    fun popShouldGiveTheTopEntryOfTheStackAndReturnASetWithoutIt(){
        val newEntry = ParseStackEntry(secondRuleSet, ParseTreeNode.TerminalNode(Symbol.Terminal("1"), "1"))

        val parseStack = ParseStack(listOf(
            Transition(firstRuleSet),
            Transition(secondRuleSet)
        ), listOf(newEntry))

        val poppedEntry: Pair<ParseStackEntry, ParseStack> = parseStack.pop()
        Assert.assertEquals(newEntry, poppedEntry.first)
        Assert.assertEquals(ParseStackEntry(firstRuleSet, ParseTreeNode.StartNode), poppedEntry.second.top())
    }

    @Test
    fun shouldPushInputAndNextRuleSetOnStackIfTransitionForRuleSetAndInputOnTopOfStacksIsShift(){

        val parseStack = ParseStack(listOf(
            Transition(firstRuleSet) { Action.Shift(secondRuleSet) },
            Transition(secondRuleSet)
        ))

        val parseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "1")
        val inputStack:List<ParseTreeNode> = listOf(parseTreeNode)

        val (nextParseStack, nextInputStack) = parseStack.process(inputStack)

        Assert.assertEquals(ParseStackEntry(secondRuleSet,parseTreeNode), nextParseStack.top())
        Assert.assertEquals(0, nextInputStack.size)

    }

    @Test
    fun shouldReduceTheStackToGivenRuleAndPushItToInputStackIfTransitionForRuleSetAndInputOnTopOfStacksIsReduce(){
        val parseStack = ParseStack(listOf(
            Transition(firstRuleSet),
            Transition(secondRuleSet) {Action.Reduce(secondRuleSet.first())}
        ), listOf(ParseStackEntry(secondRuleSet, ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "1"))))

        val parseTreeNode = ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "1")
        val inputStack:List<ParseTreeNode> = listOf(parseTreeNode)

        val (nextParseStack, nextInputStack) = parseStack.process(inputStack)
        Assert.assertEquals(ParseStackEntry(firstRuleSet,ParseTreeNode.StartNode), nextParseStack.top())
        Assert.assertEquals(ParseTreeNode.NonTerminalNode(secondRule, listOf(parseTreeNode)), nextInputStack.first())
    }

    @Test
    fun shouldPushTheTopOfInputAndWithRuleSpecifiedInActionIfTransitionForRuleSetAndInputOnTopOfStacksIsGoto(){
        val parseStack = ParseStack(listOf(
            Transition(firstRuleSet) {Action.Goto(secondRuleSet)},
            Transition(secondRuleSet)
        ))

        val parseTreeNode = ParseTreeNode.NonTerminalNode(Rule(Symbol.NonTerminal("E"), Symbol.Terminal("1").toRuleDefinition()), listOf(ParseTreeNode.TerminalNode(
            Symbol.Terminal("1"),"1"
        )))
        val inputStack:List<ParseTreeNode> = listOf(parseTreeNode)

        val (nextParseStack, nextInputStack) = parseStack.process(inputStack)
        Assert.assertEquals(ParseStackEntry(secondRuleSet,parseTreeNode), nextParseStack.top())
        Assert.assertEquals(0, nextInputStack.count())
    }
}