package com.anaykamat.languages.maya

sealed class Option<out T> {
    data class Some<T>(val data:T):Option<T>()
    object None:Option<Nothing>()
}

fun <T,O> Option<T>.map(f:(T) -> O):Option<O> = when(this){
    is Option.Some -> Option.Some(f(this.data))
    else -> Option.None
}

fun <T,O> Option<T>.fold(default:() -> Option<O>, f:(T) -> Option<O>):Option<O> = when(this){
    is Option.Some -> f(data)
    else -> default()
}
