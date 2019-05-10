package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Symbol

fun List<Rule>.symbolsBeingRead():List<Symbol> = this.map { it.currentSymbol() }.distinct()
fun List<Rule>.rulesReading(symbol:Symbol):List<Rule> = this.filter { it.currentSymbol() == symbol }

fun List<Symbol>.nextTransitionsFor(initialSet:List<Rule>,
                                    grammer:Grammer,
                                    nextStates:List<Pair<Symbol, List<Rule>>> = emptyList()
                                    ):List<Pair<Symbol, List<Rule>>>{
    val pairOfTopSymbolAndRemainingSymbols = this.pop() ?: return nextStates
    val (symbolToConsider, nextSymbols) = pairOfTopSymbolAndRemainingSymbols
    val rulesReadingCurrentSymbol = initialSet.rulesReading(symbolToConsider).map { it.readNext() }
    val closureList = grammer.closuresFor(rulesReadingCurrentSymbol)
    return nextSymbols.nextTransitionsFor(initialSet, grammer,nextStates+listOf(Pair(symbolToConsider, rulesReadingCurrentSymbol+closureList)))
}

fun List<Rule>.nextTransitions(grammer: Grammer):List<Pair<Symbol, List<Rule>>> {

    val symbolsBeingRead = this.symbolsBeingRead()
    return symbolsBeingRead.nextTransitionsFor(this, grammer)
}