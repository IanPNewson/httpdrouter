import fi.iki.elonen.NanoHTTPD.IHTTPSession
import kotlin.reflect.typeOf

fun IHTTPSession.get(name :String) :String? {
    if (!this.parameters.containsKey(name)) return null

    return this.parameters[name]?.get(0)
}

inline fun <reified T> IHTTPSession.get(name :String) :T {
    val rawValue = this.get(name)
        ?: when {
            typeOf<T>().isMarkedNullable -> {
                return null as T
            }
            else -> throw UnsupportedOperationException("Type ${T::class} cannot be null, yet no value was provided for argument '$name'")
        }

    var value :Any? = null

    value = when (T::class) {
        String::class -> rawValue
        Int::class -> rawValue.toInt()
        Float::class -> rawValue.toFloat()
        Double::class -> rawValue.toDouble()
        else -> throw UnsupportedOperationException("Cannot automatically convert to type ${T::class}")
    }

    return value as T
}