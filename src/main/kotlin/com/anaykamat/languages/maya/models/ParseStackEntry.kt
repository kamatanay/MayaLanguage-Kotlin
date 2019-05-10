package com.anaykamat.languages.maya.models

import com.anaykamat.languages.maya.Rule

data class ParseStackEntry(val ruleSet:List<Rule>, val parseTreeNode: ParseTreeNode)