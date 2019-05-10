package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.Symbol.*
import com.anaykamat.languages.maya.models.plus
import org.junit.Assert.*
import org.junit.Test

class RuleTest {

    @Test
    fun emptyRuleShouldBeParsed(){
        val rule = Rule(NonTerminal("E"))
        assertTrue(rule.isParsed())
    }

    @Test
    fun processedRuleShouldReturnEndAsCurrentSymbol(){
        val rule = Rule(NonTerminal("E"))
        val symbol = rule.currentSymbol()
        assertEquals(Symbol.END, symbol)
    }

    @Test
    fun addDefinitionShouldReturnNewRule(){
        val rule = Rule(NonTerminal("E"))
        val newRule:Rule = rule.addDefinition(Terminal("x"))
        assertNotEquals(rule, newRule)
    }

    @Test
    fun addingEndShouldReturnSameRule(){
        val rule = Rule(NonTerminal("E"))
        val newRule = rule.addDefinition(END)
        assertEquals(rule, newRule)
    }

    @Test
    fun itShouldReturnTheCurrentSymbolForRule(){
        val rule = Rule(NonTerminal("E")).addDefinition(NonTerminal("A"))
        assertEquals(NonTerminal("A"), rule.currentSymbol())
    }

    @Test
    fun readNextShouldGiveANewRuleThatWillProvideNextSymbolOfRule(){
        val rule = Rule(NonTerminal("E")).addDefinition(NonTerminal("A") + NonTerminal("B"))
        val newRule:Rule = rule.readNext()
        assertNotEquals(rule, newRule)
        assertEquals(NonTerminal("B"), newRule.currentSymbol())
    }

    @Test
    fun rulesShouldNotBeParsedIfCurrentSymbolIsNotEnd(){
        val rule = Rule(NonTerminal("E")).addDefinition(NonTerminal("A") + NonTerminal("B"))
        assertEquals(false, rule.isParsed())
    }

    @Test
    fun rulesShouldBeEqualIfTheirTermSymbolAndDefinitionIsSame(){
        val rule = Rule(NonTerminal("E")).addDefinition(NonTerminal("A") + NonTerminal("B"))
        val anotherRule = Rule(NonTerminal("E")).addDefinition(NonTerminal("A") + NonTerminal("B"))
        assertEquals(rule, anotherRule)
    }

    @Test

    fun shouldProvideElegantWayToDefineRule(){
        val rule = Rule(NonTerminal("E")).addDefinition(NonTerminal("A") + NonTerminal("B"))
        val anotherRule = RuleFor("E") with NonTerminal("A") + NonTerminal("B")
        assertEquals(rule, anotherRule)
    }

    @Test
    fun shouldReturnTheSymbolForWhichRuleIsDefined(){
        val rule = RuleFor("E") with NonTerminal("A") + NonTerminal("B")
        assertEquals(NonTerminal("E"), rule.ruleFor())
    }

    @Test
    fun originalRuleShouldGiveRuleWithStartPosition(){
        val rule = RuleFor("E") with NonTerminal("A") + NonTerminal("B")
        assertEquals(rule, rule.readNext().originalRule())
    }


}