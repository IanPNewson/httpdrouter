import controllers.Controller
import dependencyinjection.DIContext
import fi.iki.elonen.NanoHTTPD
import routes.*
import routes.authentication.RedirectAuthenticationFailedHandler
import kotlinx.coroutines.*

fun main() {

    val diContext = DIContext()

    //launch(DIContextElement(diContext)) {
        var staticRoutes =
            Router.createRouteTreeFromDirectory("C:\\Users\\Ian\\source\\repos\\AndroidProjects\\Remoting\\server\\webapp\\")
                    as Directory

        staticRoutes.addDefaultDocuments()

        var staticRouter = Router(staticRoutes)

        val sampleScreen = staticRouter.findRoute("/images/screen_sample.png")?.route!!

        val actions = Directory.root(
            Action("screenlastupdate") { _ ->
                gson(object {
                    val LastUpdate = 0
                })
            },
            AliasRoute("screen", sampleScreen),
            ControllerRoute(path = "button", clazz = ButtonController::class.java)
        )

        val routes = staticRoutes.merge(actions) as Directory

        println(routes.toString())

        diContext.apply {
            add { -> Router(routes, defaultAuthFailedHandler = RedirectAuthenticationFailedHandler("/login")) }
            add { router: Router, diContext :DIContext -> WebApp(router, diContext) }
            add { -> this }
            //add { -> ButtonController() }
            add<Nothing1> { -> object :Nothing1 {} }
        }

        val t2 = diContext.get { di: DIContext ->
            T2(di)
        }

        val app = diContext.get<WebApp>()

        Thread {
            app.start()
        }.start()

    //}

    println("Running")
    System.console().readLine()
}

class T2(val di :DIContext)


class ButtonController(val thing :Nothing2) : Controller() {
    override fun getResponse(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return text("hi from button")
    }
}

interface Nothing1
class Nothing2(val n :Nothing1)

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