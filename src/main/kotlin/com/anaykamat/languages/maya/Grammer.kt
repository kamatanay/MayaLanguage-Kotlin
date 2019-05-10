package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Symbol

data class Grammer(private val rules:List<Rule> = emptyList()) {

    companion object {
        fun from(vararg rule:Rule):Grammer = Grammer(rule.toList())
    }

    fun rulesFor(symbol: Symbol.NonTerminal): List<Rule> = rules.rulesFor(symbol).first

    fun initialSet(): List<Rule> = (RuleFor("START") with rules.first().ruleFor()).let { listOf(it) }.let {
        it.reachableList(emptyList(), rules).first
    }

    private fun List<Rule>.reachableList(finalList:List<Rule>, grammerRulesToBeConsidered:List<Rule>):Pair<List<Rule>, List<Rule>>{
        val firstInCurrentListPair = this.pop() ?: return Pair(finalList, grammerRulesToBeConsidered)
        val (ruleToConsider, listToConsider) = firstInCurrentListPair
        val (rulesConsideredForCurrentSymbol, rulesNotConsideredForCurrentSymbol) = grammerRulesToBeConsidered.rulesFor(ruleToConsider.currentSymbol())
        return (listToConsider+rulesConsideredForCurrentSymbol).reachableList(finalList+ listOf(ruleToConsider), rulesNotConsideredForCurrentSymbol)
    }

    private fun List<Rule>.closureList(finalList:List<Rule>, grammerRulesToBeConsidered:List<Rule>):Pair<List<Rule>, List<Rule>>{
        val firstInCurrentListPair = this.pop() ?: return Pair(finalList, grammerRulesToBeConsidered)
        val (ruleToConsider, listToConsider) = firstInCurrentListPair
        val (rulesConsideredForCurrentSymbol, rulesNotConsideredForCurrentSymbol) = grammerRulesToBeConsidered.rulesFor(ruleToConsider.currentSymbol())
        return (listToConsider+rulesConsideredForCurrentSymbol).closureList(finalList+ rulesConsideredForCurrentSymbol, rulesNotConsideredForCurrentSymbol)
    }

    fun closuresFor(rules: List<Rule>): List<Rule> = rules.closureList(emptyList(), this.rules).first

}

fun <T> List<T>.pop():Pair<T, List<T>>? = if (this.isNotEmpty()) Pair(this.first(), this.drop(1)) else null

fun List<Rule>.rulesFor(symbol: Symbol): Pair<List<Rule>, List<Rule>> = this.filter { it.ruleFor() == symbol }.let { rulesForSymbol ->
    Pair(rulesForSymbol, this.filter { !rulesForSymbol.contains(it) })
}
