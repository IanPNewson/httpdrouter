import routes.Action
import routes.Directory
import routes.Router

fun main(args: Array<String>) {

    var routes = Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")
        as Directory

        /*Directory("")
        .addChildren(
            Directory("hielo")
                .addChildren(routes.Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")),
            Directory("industrious")
                .addChildren(routes.Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\htmltemplate.zip"))
        )*/

    routes.addDefaultDocuments()

    val actions = Directory.root()
        .addChildren(
            Directory("actions",
                    Action("time") {
                        session -> text("${System.currentTimeMillis()}")
                    }
                )
        )

    routes = routes.merge(actions) as Directory

//    routes.merge(Directory.root()
//        .addChildren(Action("test", null)))

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