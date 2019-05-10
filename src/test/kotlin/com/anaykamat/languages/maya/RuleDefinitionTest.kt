package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.RuleDefinition
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.toRuleDefinition
import org.junit.Assert
import org.junit.Test

class RuleDefinitionTest {

    @Test
    fun shouldBeEqualIfTheyHaveSameSymbolsAndPosition(){
        val ruleDefinition = Symbol.Terminal("a").toRuleDefinition() + Symbol.NonTerminal("A")
        val anotherRuleDefinition = RuleDefinition() + Symbol.Terminal("a") + Symbol.NonTerminal("A")
        Assert.assertEquals(ruleDefinition, anotherRuleDefinition)
    }

    @Test
    fun shouldNotBeEqualIfTheyHaveDifferentSymbols(){
        val ruleDefinition = Symbol.Terminal("a").toRuleDefinition() + Symbol.NonTerminal("A")
        val anotherRuleDefinition = RuleDefinition() + Symbol.Terminal("a") + Symbol.NonTerminal("B")
        Assert.assertNotEquals(ruleDefinition, anotherRuleDefinition)
    }

    @Test
    fun shouldNotBeEqualIfTheyHaveDifferentPositions(){
        val ruleDefinition = Symbol.Terminal("a").toRuleDefinition() + Symbol.NonTerminal("A")
        val anotherRuleDefinition = Symbol.Terminal("a").toRuleDefinition() + Symbol.NonTerminal("A")
        Assert.assertNotEquals(ruleDefinition, anotherRuleDefinition.readNext())
    }

    @Test
    fun shouldGiveSameDefinitionIfReadNextIsCalledBeyondEnd(){
        val ruleDefinition = Symbol.Terminal("a").toRuleDefinition() + Symbol.NonTerminal("A")
        val anotherRuleDefinition = Symbol.Terminal("a").toRuleDefinition() + Symbol.NonTerminal("A")
        Assert.assertEquals(ruleDefinition.readNext().readNext(), anotherRuleDefinition.readNext().readNext().readNext().readNext())
    }
}