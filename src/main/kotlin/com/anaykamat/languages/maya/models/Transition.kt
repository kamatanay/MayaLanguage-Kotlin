package com.anaykamat.languages.maya.models

import com.anaykamat.languages.maya.Rule

data class Transition(val ruleSet:List<Rule>, val transition: (Symbol) -> Action = {Action. None})