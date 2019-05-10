package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Symbol.NonTerminal
import com.anaykamat.languages.maya.models.Symbol.Terminal
import com.anaykamat.languages.maya.models.plus
import org.junit.Assert
import org.junit.Test

class GrammerTest {

    private val grammer:Grammer = Grammer.from (
        RuleFor("E") with NonTerminal("E") + Terminal("+") + NonTerminal("B"),
        RuleFor("E") with NonTerminal("B"),
        RuleFor("B") with Terminal("0"),
        RuleFor("B") with Terminal("1")
    )


    @Test
    fun itShouldProvideListOfRuleForGivenSymbol(){
        val rulesForB:List<Rule> = grammer.rulesFor(NonTerminal("B"))
        listOf(
            RuleFor("B") with Terminal("0"),
            RuleFor("B") with Terminal("1")
        ).let {
            Assert.assertEquals(it, rulesForB)
        }
    }

    @Test
    fun itShouldProvideInitialParseSetWithAgumentedRuleAsFirstItem(){
        val initialSet:List<Rule> = grammer.initialSet()
        Assert.assertEquals(RuleFor("START") with NonTerminal("E"), initialSet.first())
    }

    @Test
    fun initialParseSetShouldIncludeAllRulesForTerminalIdentifiedByAugmentedRule(){
        val initialSet:List<Rule> = grammer.initialSet()
        listOf(
            RuleFor("E") with NonTerminal("E") + Terminal("+") + NonTerminal("B"),
            RuleFor("E") with NonTerminal("B")
        ).let {
            Assert.assertTrue(initialSet.subList(1,3) == it)
        }
    }


    @Test
    fun initialParseSetShouldIncludeClosuresOfItemsConsidered(){
        val initialSet:List<Rule> = grammer.initialSet()
        listOf(
            RuleFor("B") with Terminal("0"),
            RuleFor("B") with Terminal("1")
        ).let {
            Assert.assertTrue(initialSet.subList(3,5) == it)
        }
    }

    @Test
    fun itShouldProvideClosuresForGivenRuleList(){
        val rules = listOf(
            (RuleFor("E") with NonTerminal("E") + Terminal("+") + NonTerminal("B")).readNext().readNext(),
            (RuleFor("B") with Terminal("0")).readNext()
        )

        val closureList:List<Rule> = grammer.closuresFor(rules)


        listOf(
            RuleFor("B") with Terminal("0"),
            RuleFor("B") with Terminal("1")
        ).let {
            Assert.assertTrue(closureList == it)
        }

    }

}