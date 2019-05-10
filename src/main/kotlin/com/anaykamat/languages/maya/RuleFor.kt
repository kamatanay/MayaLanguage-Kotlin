package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.RuleDefinition
import com.anaykamat.languages.maya.models.Symbol
import com.anaykamat.languages.maya.models.toRuleDefinition

data class RuleFor(val name:String){
    infix fun with(definition: RuleDefinition):Rule = Rule(Symbol.NonTerminal(name), definition)
    infix fun with(symbol: Symbol):Rule = Rule(Symbol.NonTerminal(name), symbol.toRuleDefinition())
}