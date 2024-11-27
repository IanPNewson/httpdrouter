package org.iannewson.httpdrouter

import fi.iki.elonen.NanoHTTPD.IHTTPSession
import kotlin.reflect.typeOf

fun IHTTPSession.get(name :String) :String? {
    if (!this.parameters.containsKey(name)) return null

    return this.parameters[name]?.get(0)
}

inline fun <reified T> IHTTPSession.get(name: String): T {
    val rawValue = this.get(name)
        ?: when {
            typeOf<T>().isMarkedNullable -> {
                return null as T
            }
            else -> throw UnsupportedOperationException("Type ${T::class} cannot be null, yet no value was provided for argument '$name'")
        }

    val value: Any? = when {
        T::class == String::class -> rawValue
        T::class == Int::class -> rawValue.toInt()
        T::class == Long::class -> rawValue.toLong()
        T::class == Short::class -> rawValue.toShort()
        T::class == Byte::class -> rawValue.toByte()
        T::class == Boolean::class -> rawValue.toBooleanStrictOrNull()
            ?: throw IllegalArgumentException("Value '$rawValue' is not a valid Boolean")
        T::class == Float::class -> rawValue.toFloat()
        T::class == Double::class -> rawValue.toDouble()
        T::class == Char::class -> if (rawValue.length == 1) rawValue[0] else throw IllegalArgumentException("Value '$rawValue' is not a valid Char")
        T::class.java.isEnum -> {
            val enumConstants = T::class.java.enumConstants as Array<Enum<*>>
            enumConstants.firstOrNull { it.name == rawValue }
                ?: throw IllegalArgumentException("Value '$rawValue' is not a valid constant for enum ${T::class}")
        }
        else -> throw UnsupportedOperationException("Cannot automatically convert to type ${T::class}")
    }

    return value as T
}

