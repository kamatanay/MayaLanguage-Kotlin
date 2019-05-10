package com.anaykamat.languages.maya.models

data class RuleDefinition(private val symbols:List<Symbol> = emptyList(), private val currentPosition:Int = 0) {

    operator fun plus(symbol:Symbol) = this.copy(symbols = symbols + listOf(symbol))
    operator fun plus(ruleDefinition: RuleDefinition) = this.copy(symbols = symbols + ruleDefinition.symbols)
    fun currentSymbol(): Symbol = symbols.symbolAt(currentPosition)
    fun readNext():RuleDefinition = if (currentSymbol() == Symbol.END) this else this.copy(currentPosition = currentPosition + 1)
    fun length():Int = symbols.count()
}

fun List<Symbol>.symbolAt(index:Int) = this.elementAtOrElse(index) { Symbol.END }

fun Symbol.toRuleDefinition() = RuleDefinition(listOf(this), 0)

operator fun Symbol.plus(symbol:Symbol):RuleDefinition = this.toRuleDefinition() + symbol