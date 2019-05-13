package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.ParseTreeNode
import com.anaykamat.languages.maya.models.Symbol

class Tokenizer(private val tokenizerMap: List<Pair<Symbol.Terminal, Regex>>) {
    fun tokenize(inputString: String, finalTokens:List<ParseTreeNode> = emptyList()): List<ParseTreeNode> {

        val matchTokenAndString:Option<Triple<Symbol, String, Int>> = tokenizerMap.map { tokenMap ->
            val matchedValue = tokenMap.second.find(inputString)?.let { Option.Some(it) } ?: Option.None
            matchedValue.map { matchResult -> Triple(tokenMap.first, matchResult.groupValues.last(), matchResult.groupValues.first().count())  }
        }.find { it is Option.Some } ?: Option.None

        return matchTokenAndString.map { matchPair ->
            val symbol = matchPair.first
            val value = matchPair.second
            val lengthOfMatch = matchPair.third
            val nextString = inputString.substring(lengthOfMatch).trimStart()
            Triple(symbol, value, nextString)
        }.fold({
            Option.Some(finalTokens)
        },{
            Option.Some(tokenize(it.third, finalTokens+listOf(ParseTreeNode.TerminalNode(it.first, it.second))))
        }).let {
            it as Option.Some
        }.let {
            it.data
        }

    }
}