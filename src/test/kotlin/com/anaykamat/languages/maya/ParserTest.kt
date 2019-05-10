package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.RuleDefinition
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.plus
import org.junit.Assert
import org.junit.Test

class ParserTest {
    @Test
    fun itShouldGenerateListOfPairsOfSymbolAndNextStateFromGivenParseSetAndGrammer(){
        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = grammer.initialSet()
        val nextTransitions:List<Pair<Symbol, List<Rule>>> = initialSet.nextTransitions(grammer)

        Assert.assertEquals(listOf(Symbol.NonTerminal("E")), nextTransitions.map { it.first })
    }

    @Test
    fun nextStatesShouldHaveRulesFromCallingStateWithWhereCurrentSymbolIsGivenSymbol(){
        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = listOf(RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B"))
        val nextTransitions:List<Pair<Symbol, List<Rule>>> = initialSet.nextTransitions(grammer)

        Assert.assertEquals(
            listOf(RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B")),
            nextTransitions.first().second.map { it.originalRule() }.subList(0,1))
    }

    @Test
    fun rulesTakenFromCallingSetInTheNextTransitionStateForGivenSymbolShouldBeInPositionToReadTheSymbolAfterGivenSymbol(){
        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = listOf(RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B"))
        val nextTransitions:List<Pair<Symbol, List<Rule>>> = initialSet.nextTransitions(grammer)

        Assert.assertEquals(
            listOf(Rule(Symbol.NonTerminal("E"), RuleDefinition(listOf(Symbol.NonTerminal("E"),Symbol.NonTerminal("B")), 1))),
            nextTransitions.first().second.subList(0,1))
    }

    @Test
    fun nextTransitionStateForGivenSymbolShouldIncludeClosuresReadRulesFromCallingState(){
        val grammer:Grammer = Grammer.from (
            RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B"),
            RuleFor("B") with Symbol.Terminal("1")
        )
        val initialSet = listOf(RuleFor("E") with Symbol.NonTerminal("E") + Symbol.NonTerminal("B"))
        val nextTransitions:List<Pair<Symbol, List<Rule>>> = initialSet.nextTransitions(grammer)

        Assert.assertEquals(
            listOf(Rule(Symbol.NonTerminal("B"), RuleDefinition(listOf(Symbol.Terminal("1")), 0))),
            nextTransitions.first().second.subList(1,2))
    }
}