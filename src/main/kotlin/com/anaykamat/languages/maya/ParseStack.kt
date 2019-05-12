package com.anaykamat.languages.maya

import com.anaykamat.languages.maya.models.*

class ParseStack(val actionMap:List<Transition>, private val list:List<ParseStackEntry> = emptyList()){

    fun top():ParseStackEntry{
        return list.getOrElse(0) {actionMap.first().ruleSet.let { ruleSet -> ParseStackEntry(ruleSet, ParseTreeNode.StartNode) }}
    }

    fun push(parseStackEntry: ParseStackEntry): ParseStack {
        return ParseStack(actionMap, listOf(parseStackEntry)+list)
    }

    fun pop(): Pair<ParseStackEntry, ParseStack> {
        val poppedList = list.pop() ?: return ParseStack(actionMap).let { Pair(it.top(), it) }
        return Pair(poppedList.first, ParseStack(actionMap, poppedList.second))
    }

    fun count(): Int {
        return list.count()
    }

}

fun ParseStack.process(inputStack:List<ParseTreeNode>):Pair<ParseStack, List<ParseTreeNode>>{
    val (ruleSet, parseTreeNode) = this.top()
    val (_, transition) = this.actionMap.find { transition -> transition.ruleSet == ruleSet } ?: Transition(ruleSet)
    val firstEntry:ParseTreeNode = inputStack.getOrElse(0) {ParseTreeNode.StartNode}
    val action = when(firstEntry){
        is ParseTreeNode.NonTerminalNode -> transition.invoke(firstEntry.rule.ruleFor())
        is ParseTreeNode.TerminalNode -> transition.invoke(firstEntry.symbol)
        is ParseTreeNode.StartNode -> transition.invoke(Symbol.END)
    }
    return when(action){
        is Action.Shift -> {
            val (_, nextList) = inputStack.pop() ?: Pair(firstEntry, emptyList())
            Pair(push(ParseStackEntry(action.set, firstEntry)), nextList)
        }
        is Action.Reduce -> {
            val fold = (0 until action.rule.length()).fold(
                Pair<List<ParseStackEntry>, ParseStack>(emptyList(), this)
            ) { pair, index ->
                pair.second.pop().let { Pair(pair.first+listOf(it.first), it.second) }
            }
            val (inputValues, stack) = fold
            Pair(stack, listOf(ParseTreeNode.NonTerminalNode(action.rule, inputValues.reversed().map { it.parseTreeNode }))+inputStack)
        }
        is Action.Goto -> {
            val (_, nextList) = inputStack.pop() ?: Pair(firstEntry, emptyList())
            Pair(push(ParseStackEntry(action.set, firstEntry)), nextList)
        }
        else -> Pair(this, inputStack)
    }
}

fun ParseStack.build(processFunction:ParseStack.(List<ParseTreeNode>) -> Pair<ParseStack, List<ParseTreeNode>>):(List<ParseTreeNode>) -> ParseStack{
    return { inputStack: List<ParseTreeNode> ->
        if (inputStack.count() == 0 && this.count() == 1){
            this
        } else {
            val (newParseStack, newInputStack) =  processFunction(this, inputStack)
            newParseStack.build(processFunction)(newInputStack)
        }
    }
}