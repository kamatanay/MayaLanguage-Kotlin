package com.anaykamat.languages.maya.models

import com.anaykamat.languages.maya.Rule

sealed class ParseTreeNode {

    object StartNode:ParseTreeNode()
    data class NonTerminalNode(val rule: Rule, val inputs:List<ParseTreeNode>):ParseTreeNode()
    data class TerminalNode(val symbol: Symbol, val input:String):ParseTreeNode()

}