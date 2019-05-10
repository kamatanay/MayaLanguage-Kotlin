package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.RuleDefinition
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.Symbol.*
import com.anaykamat.languages.maya.models.toRuleDefinition

data class Rule(val nonTerminal: NonTerminal, private val definition:RuleDefinition = RuleDefinition()) {
    fun isParsed(): Boolean = currentSymbol() == END

    fun currentSymbol(): Symbol = definition.currentSymbol()

    fun length():Int = definition.length()

    fun addDefinition(symbol:END):Rule = this

    fun addDefinition(terminal: Symbol): Rule = Rule(nonTerminal, definition + terminal.toRuleDefinition())

    fun addDefinition(definition: RuleDefinition): Rule = Rule(nonTerminal, definition.copy(currentPosition = 0))

    fun readNext(): Rule = Rule(nonTerminal, definition.readNext())
    fun ruleFor(): NonTerminal = nonTerminal
    fun originalRule(): Rule = this.copy(nonTerminal = nonTerminal, definition = definition.copy(currentPosition = 0))
}
