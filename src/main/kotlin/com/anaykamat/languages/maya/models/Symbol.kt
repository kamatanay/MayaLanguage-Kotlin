package com.anaykamat.languages.maya.models

sealed class Symbol {
    data class NonTerminal(val name:String):Symbol()
    object END:Symbol()
    data class Terminal(val name:String):Symbol()
}