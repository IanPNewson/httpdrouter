package routes

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response
import routes.authentication.Authenticator
import kotlin.reflect.KFunction
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

// routes.Action class that supports automatic parameter binding with Enum support
open class Action(
    path: String,
    authenticationHandler: Authenticator? = null,
    private val handler: (session: IHTTPSession) -> Response
) : Route(path, authenticationHandler = authenticationHandler) {

    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handler(session)

//        val args = mutableMapOf<kotlin.reflect.KParameter, Any?>()

        TODO()
        // Bind parameters based on the function's signature
//        handler.valueParameters.forEach { parameter ->
//            val name = parameter.name
//            val type = parameter.type.jvmErasure
//
//            // Find matching parameter in the query string
//            val value = session.parameters[name]
//
//            // Convert the parameter if possible
//            args[parameter] = when {
//                value == null -> null // Use default or null if not found
//                type == String::class -> value
//                type == Int::class -> throw RuntimeException("TODO")
//                type == Boolean::class -> throw RuntimeException("TODO")
//
//                else -> null // Unsupported type
//            }
//        }

//        return handler.callBy(args)
    }

    // Helper function to convert a string to an Enum value
    private fun <T : Enum<*>> enumValueOrNull(enumClass: Class<T>, value: String?): T? {
        return value?.let {
            enumClass.enumConstants.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }

}

//class MyAction(path: String) : Action(path, { session -> this.handleRequest(session) }) {
//    fun handleRequest(session: IHTTPSession): Response {
//        TODO()
//    }
//}
