package com.anaykamat.languages.maya.models

import com.anaykamat.languages.maya.Rule

sealed class Action {
    data class Shift(val set:List<Rule>):Action()
    data class Goto(val set:List<Rule>):Action()
    data class Reduce(val rule:Rule):Action()
    object None:Action()
}