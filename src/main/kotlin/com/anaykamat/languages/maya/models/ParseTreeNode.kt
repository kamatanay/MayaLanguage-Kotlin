package com.anaykamat.languages.maya.models

import com.anaykamat.languages.maya.Rule

sealed class ParseTreeNode(open val context:Map<String, ParseTreeNode> = emptyMap()) {
    object StartNode:ParseTreeNode()
    data class NonTerminalNode(val rule: Rule, val inputs:List<ParseTreeNode>, override val context:Map<String, ParseTreeNode> = emptyMap()):ParseTreeNode(context)
    data class TerminalNode(val symbol: Symbol, val input:String, override val context:Map<String, ParseTreeNode> = emptyMap()):ParseTreeNode(context)
}