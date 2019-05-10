package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Action
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.Transition
import com.anaykamat.languages.maya.models.plus
import org.junit.Assert
import org.junit.Test

class ParseTableTest{

    @Test
    fun setsThatGenerateNewNonEmptySetShouldGenerateShiftActionToTheNextTransitionStateForTerminalSymbol(){

        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = listOf((RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B")).readNext())
        val actions:Transition = ParseTable().context {  initialSet.actions(grammer) }.first

        Assert.assertEquals(Action.Shift(initialSet.nextTransitions(grammer).first().second), actions.transition(Symbol.Terminal("+")))

    }

    @Test
    fun setsThatGenerateNewNonEmptySetShouldGenerateGotoActionToTheNextTransitionStateForNonTerminalSymbol(){

        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = listOf((RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B")).readNext().readNext())
        val actions:Transition = ParseTable().context {  initialSet.actions(grammer) }.first

        Assert.assertEquals(Action.Goto(initialSet.nextTransitions(grammer).first().second), actions.transition.invoke(Symbol.NonTerminal("B")))

    }

    @Test
    fun setsWithRuleReadingEndShouldGenerateReduceActionForGivenRule(){

        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = listOf((RuleFor("B") with Symbol.Terminal("1")).readNext())
        val actions:Transition = ParseTable().context {  initialSet.actions(grammer) }.first

        Assert.assertEquals(Action.Reduce(RuleFor("B") with Symbol.Terminal("1")),
            actions.transition.invoke(Symbol.END))

    }

    @Test
    fun itShouldAlsoReturnListOfNewSetsGenerated(){
        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = listOf(
            (RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B")),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val nextSets:List<List<Rule>> = ParseTable().context {  initialSet.actions(grammer) }.second

        Assert.assertEquals(
            listOf(
                listOf((RuleFor("E") with Symbol.NonTerminal("E") + Symbol.Terminal("+") + Symbol.NonTerminal("B")).readNext()),
                listOf((RuleFor("B") with Symbol.Terminal("1")).readNext())
            ),nextSets)
    }

}