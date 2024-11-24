import routes.*
import routes.authentication.Allow
import routes.authentication.Deny
import routes.authentication.RedirectAuthenticationFailedHandler

fun main() {

    var staticRoutes = Router.createRouteTreeFromDirectory("C:\\Users\\Ian\\source\\repos\\AndroidProjects\\Remoting\\server\\webapp\\")
        as Directory

    staticRoutes.addDefaultDocuments()

    var staticRouter = Router(staticRoutes)

    val sampleScreen = staticRouter.findRoute("/images/screen_sample.png")?.route!!

    val actions = Directory.root(
            Action("screenlastupdate") { _ -> gson(object {
                val LastUpdate = 0
            })},
            AliasRoute("screen", sampleScreen)
        )

    val routes = staticRoutes.merge(actions) as Directory

    println(routes.toString())



    val diContext = DIContext()
        .apply {
            add{ -> Router(routes, defaultAuthFailedHandler = RedirectAuthenticationFailedHandler("/login")) }
            add{ router : Router -> WebApp(router) }
            add {->this }
        }

    val app = diContext.get<WebApp>()

    Thread {
        app.start()
    }.start()

    println("Running")
    System.console().readLine()
}


class Test(val ting :Thing)

class Thing

/*
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