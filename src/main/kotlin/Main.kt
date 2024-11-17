import routes.Directory
import routes.Router

fun main(args: Array<String>) {

    val routes = Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")

        /*Directory("")
        .addChildren(
            Directory("hielo")
                .addChildren(routes.Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")),
            Directory("industrious")
                .addChildren(routes.Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\htmltemplate.zip"))
        )*/

    (routes as Directory).addDefaultDocuments()

    println(routes.toString())

    val diContext = DIContext()
        .apply {
            add{ -> Router(routes) }
            add{ router : Router -> WebApp(router)}
            add {->this }
        }

    val app = diContext.get<WebApp>()

    Thread {
        app.start()
    }.start()

    println("Running")
    System.console().readLine()
}

/*
// Action class that supports automatic parameter binding with Enum support
class Action(path: String, private val handler: KFunction<Response>) : routes.Route(path) {
    suspend fun invokeWithParameters(parms: Map<String, String>): Response {
        val args = mutableMapOf<kotlin.reflect.KParameter, Any?>()

        // Bind parameters based on the function's signature
        handler.valueParameters.forEach { parameter ->
            val name = parameter.name
            val type = parameter.type.jvmErasure

            // Find matching parameter in the query string
            val value = parms[name]

            // Convert the parameter if possible
            args[parameter] = when {
                value == null -> null // Use default or null if not found
                type == String::class -> value
                type == Int::class -> value.toIntOrNull()
                type == Boolean::class -> value.toBoolean()
                type.java.isEnum -> enumValueOrNull(type, value) // Enum support
                else -> null // Unsupported type
            }
        }

        return if (handler.isSuspend) {
            handler.callSuspendBy(args)
        } else {
            handler.callBy(args)
        }
    }

    // Helper function to convert a string to an Enum value
    private fun <T : Enum<*>> enumValueOrNull(enumClass: Class<T>, value: String?): T? {
        return value?.let {
            enumClass.enumConstants.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }
}
// Example Enum
enum class UserRole { ADMIN, USER, GUEST }

// Example handler with Enum and other parameters
fun loginHandler(username: String?, role: UserRole?): Response {
    return if (username == "admin" && role == UserRole.ADMIN) {
        Response(Status.OK, MIME_PLAINTEXT, "Welcome, admin!")
    } else {
        Response(Status.UNAUTHORIZED, MIME_PLAINTEXT, "Access denied for $role")
    }
}
*/