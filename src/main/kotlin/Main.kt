fun main(args: Array<String>) {

    //Base path
    val baseDir = "C:\\Users\\Ian\\Downloads\\html-css-template-pfnp\\html-css-template-pfnp"

    var routes = Directory("/")
        .addChildren(
            StaticFile("", "$baseDir\\index.html")
        )

    routes = Router.createRouteTreeFromDirectory(baseDir)

    val diContext = DIContext()
        .apply {
            add{ -> WebApp(routes)}
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
class Action(path: String, private val handler: KFunction<Response>) : Route(path) {
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

// Router that holds the root directory and finds routes
object Router {
    private lateinit var rootRoute: Directory

    fun initialize(root: Directory) {
        rootRoute = root
    }

    fun findRoute(path: String): Route? {
        val pathParts = path.trim('/').split('/')
        return findRouteRecursively(rootRoute.children, pathParts)
    }

    private fun findRouteRecursively(routes: List<Route>, pathParts: List<String>): Route? {
        if (pathParts.isEmpty()) return null
        val currentPart = pathParts.first()

        val matchedRoute = routes.firstOrNull { it.path == currentPart }
        return when (matchedRoute) {
            is Directory -> findRouteRecursively(matchedRoute.children, pathParts.drop(1))
            is StaticFile, is Action -> if (pathParts.size == 1) matchedRoute else null
            else -> null
        }
    }
}

// Function to serve different route types with parameter binding support for Action
suspend fun serveRoute(route: Route, parms: Map<String, String>): Response {
    return when (route) {
        is StaticFile -> staticFileData(route.resourceId)
        is Action -> route.invokeWithParameters(parms)
        else -> Response(Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
    }
}

// Route setup with an Action using a function with Enum parameters
fun setupRoutes(): Directory {
    return Directory("/", listOf(
        StaticFile("", R.raw.home),  // The root URL serving the home page
        Directory("images", listOf(
            StaticFile("back.png", R.drawable.back),
            StaticFile("home.png", R.drawable.home)
        )),
        StaticFile("login.html", R.raw.login),
        Action("handleClick", ::handleClick),  // Function with no parameters
        Action("login", ::loginHandler)  // Function with parameters including Enum
    ))
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

// NanoHTTPD server's serve function
override fun serve(
    uri: String?,
    method: Method?,
    headers: MutableMap<String, String>?,
    parms: MutableMap<String, String>?,
    body: MutableMap<String, String>?
): Response {
    if (!::Router.rootRoute.isInitialized) {
        Router.initialize(setupRoutes())
    }

    val path = uri?.lowercase(Locale.getDefault())?.trim('/') ?: ""
    val route = Router.findRoute(path)

    return if (route != null) {
        runBlocking { serveRoute(route, parms ?: emptyMap()) }
    } else {
        Response(Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found")
    }
}

// Placeholder function to serve static file data
fun staticFileData(resourceId: Int): NanoHTTPD.Response {
    return NanoHTTPD.Response(Status.OK, MIME_HTML, "<html><body>Static File Content</body></html>")
}
*/