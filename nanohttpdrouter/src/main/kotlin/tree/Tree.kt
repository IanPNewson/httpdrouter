package org.iannewson.httpdrouter.tree

class Tree<T>(
    val value :T,
    val children :List<Tree<T>>
) {
    constructor(value: T, vararg children :Tree<T>)
            : this(value, children.toList())
}

fun <T> Tree<T>.find(value :T): Tree<T>? {
    return this.children.firstOrNull {
        it.value == value
    }
}