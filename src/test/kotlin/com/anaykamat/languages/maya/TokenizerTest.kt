package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.ParseTreeNode
import com.anaykamat.languages.maya.models.Symbol
import org.junit.Assert
import org.junit.Test

class TokenizerTest {

    @Test
    fun itShouldParseGivenStringAccordingToProvidedMapOfSymbolAndExpressionAndReturnListOfToken(){

        val tokenizerMap = listOf(
            Symbol.Terminal("(") to """^(\()""".toRegex(),
            Symbol.Terminal(")") to """^(\))""".toRegex(),
            Symbol.Terminal(".") to """^(\.)""".toRegex(),
            Symbol.Terminal("a") to """^(a)""".toRegex(),
            Symbol.Terminal("x") to """^\"(.+)\"""".toRegex()
        )

        val tokenizer = Tokenizer(tokenizerMap)

        val tokens:List<ParseTreeNode> = tokenizer.tokenize("""( a . a. "ab1234")""")

        Assert.assertEquals(listOf(
            ParseTreeNode.TerminalNode(Symbol.Terminal("("), "("),
            ParseTreeNode.TerminalNode(Symbol.Terminal("a"), "a"),
            ParseTreeNode.TerminalNode(Symbol.Terminal("."), "."),
            ParseTreeNode.TerminalNode(Symbol.Terminal("a"), "a"),
            ParseTreeNode.TerminalNode(Symbol.Terminal("."), "."),
            ParseTreeNode.TerminalNode(Symbol.Terminal("x"), "ab1234"),
            ParseTreeNode.TerminalNode(Symbol.Terminal(")"), ")")
        ), tokens)
    }

}