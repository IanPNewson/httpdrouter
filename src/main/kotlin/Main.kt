import routes.Action
import routes.Directory
import routes.Router
import routes.authentication.Allow
import routes.authentication.Deny
import routes.authentication.RedirectAuthenticationFailedHandler

fun main() {

    var zipRoutes = Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")
        as Directory

        /*Directory("")
        .addChildren(
            Directory("hielo")
                .addChildren(routes.Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")),
            Directory("industrious")
                .addChildren(routes.Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\htmltemplate.zip"))
        )*/

    zipRoutes.addDefaultDocuments()

    val actions = Directory.root(
            Directory("actions",
                    //authenticationHandler = DenyIf { _ -> System.currentTimeMillis()%2 == 0L},
                    Action("time") {
                        _ ->
                        text("${System.currentTimeMillis()}")
                    },
                    Action("test", authenticationHandler = Deny()) {
                        val x = it.get<Float>("x")
                        val y = it.get<Float?>("y")
                        val str = it.get<String?>("str")

                        return@Action text("x: $x, y: $y, str: $str")
                    }
                ),
            authHandler = Allow()
        )

    val routes = zipRoutes.merge(actions) as Directory

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