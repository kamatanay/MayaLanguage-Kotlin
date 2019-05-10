package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.Transition
import org.junit.Assert
import org.junit.Test

class ActionMapTest {

    private val grammer:Grammer = Grammer()
    private val startingSet = listOf(Rule(Symbol.NonTerminal("B")))
    private val firstTransition = Transition(emptyList())
    private val secondTransition = Transition(emptyList())

    private val mockActionBuilder:List<Rule>.(Grammer) -> Pair<Transition, List<List<Rule>>> ={ grammer:Grammer ->
        this.let {

            if (it == startingSet){

                Pair(
                    firstTransition,
                    listOf(
                        listOf(Rule(Symbol.NonTerminal("C"))),
                        listOf(Rule(Symbol.NonTerminal("D")))
                    )
                )
            } else {
                Pair(
                    secondTransition,
                    listOf(
                        listOf(Rule(Symbol.NonTerminal("C"))),
                        listOf(Rule(Symbol.NonTerminal("D")))
                    )
                )
            }
        }
    }

    private val parseTable = ParseTable(mockActionBuilder)

    @Test
    fun itShouldRecursivelyBuildCompleteTransitionMapForDistinctSetsStartingWithTransitionForFirstSet(){

        val initialSet = listOf(listOf(Rule(Symbol.NonTerminal("B"))))
        val actionMap:List<Transition> = initialSet.actionMap(grammer, parseTable)

        Assert.assertEquals(listOf(firstTransition, secondTransition, secondTransition), actionMap)
        Assert.assertEquals(firstTransition, actionMap.first())
    }
}