package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Transition

fun List<List<Rule>>.actionMap(grammer: Grammer, parseTable:ParseTable, setsConsidered:List<List<Rule>> = emptyList(), finalMap:List<Transition> = emptyList()):List<Transition>{
    val currentRuleSetAndListOfRuleSetsToBeParsed = this.pop() ?: return finalMap
    val (ruleSet, ruleSetToBeParsed) = currentRuleSetAndListOfRuleSetsToBeParsed
    val (transition, listOfNewSets) = parseTable.context {  ruleSet.actions(grammer) }
    val newSetConsidered = setsConsidered + listOf(ruleSet)
    val distinctListOfRulesToBeParsed = (ruleSetToBeParsed+listOfNewSets).filter { !newSetConsidered.contains(it) }
    return distinctListOfRulesToBeParsed.actionMap(grammer, parseTable, newSetConsidered, finalMap+listOf(transition))
}