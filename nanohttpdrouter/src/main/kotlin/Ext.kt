package org.iannewson.httpdrouter

fun String.extension() :String? {
    if (this == null) return null
    val lastIndex = this.lastIndexOf('.')
    if (lastIndex < 0) return null

    return this.substring(lastIndex + 1)
}