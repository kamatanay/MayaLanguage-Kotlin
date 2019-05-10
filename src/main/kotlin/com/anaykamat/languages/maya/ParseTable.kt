package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.Action
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.Transition



val defaultActionBuilder:List<Rule>.(Grammer) -> Pair<Transition, List<List<Rule>>>  = {grammer:Grammer ->
    this.nextTransitions(grammer).let { nextSets ->
        nextSets.fold(
            Transition(this)
        ) { transition, pairOfSymbolAndSet ->
            val action = when(pairOfSymbolAndSet.first){
                is Symbol.NonTerminal -> Action.Goto(pairOfSymbolAndSet.second)
                Symbol.END -> Action.Reduce(pairOfSymbolAndSet.second.first { it.isParsed() }.originalRule())
                else -> Action.Shift(pairOfSymbolAndSet.second)
            }
            Transition(transition.ruleSet) { symbol ->
                when(action){
                    is Action.Reduce -> action
                    else -> if (symbol == pairOfSymbolAndSet.first) action else transition.transition.invoke(symbol)
                }
            }
        }.let {
            Pair(it, nextSets.map(Pair<Symbol, List<Rule>>::second).distinct())
        }
    }
}


class ParseTable(private val actionBuilder:List<Rule>.(grammer:Grammer) -> Pair<Transition, List<List<Rule>>> = defaultActionBuilder){
    fun context(block:ParseTable.() -> Pair<Transition, List<List<Rule>>>):Pair<Transition, List<List<Rule>>>{
        return block.invoke(this)
    }
    fun List<Rule>.actions(grammer: Grammer):Pair<Transition, List<List<Rule>>> = actionBuilder.invoke(this, grammer)
}