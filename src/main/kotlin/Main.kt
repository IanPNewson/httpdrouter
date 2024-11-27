import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.WebApp
import org.iannewson.httpdrouter.controllers.Controller
import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.responses.gson
import org.iannewson.httpdrouter.responses.text
import org.iannewson.httpdrouter.routes.*
import org.iannewson.httpdrouter.routes.authentication.RedirectAuthenticationFailedHandler
import java.util.zip.ZipFile

fun main() {

    val diContext = DIContext()

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

    diContext.apply {
        add<Route> { -> routes }
        add { -> Router(routes, defaultAuthFailedHandler = RedirectAuthenticationFailedHandler("/login")) }
        add { routes :Route -> Router(routes)}
        add { router: Router, diContext: DIContext -> WebApp(router, diContext) }
        add { -> this }
        add<Nothing1> { -> object : Nothing1 {} }
    }

    //diContext.get<Nothing3>()

    val app = diContext.get<WebApp>()

    Thread {
        app.start()
    }.start()

    println("Running")
    System.console().readLine()

}

class DebuggableZipFile(filePath: String) : ZipFile(filePath) {
    private var isClosed = false

    override fun close() {
        if (isClosed) {
            throw IllegalStateException("Attempting to close an already closed ZipFile!")
        }
        println("ZipFile is being closed")
        isClosed = true
        super.close()
    }

    fun checkIfClosed() {
        if (isClosed) {
            throw IllegalStateException("ZipFile is already closed!")
        }
    }
}


class ButtonController(val thing: Nothing2) : Controller() {
    override fun getResponse(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return text("hi from button")
    }

}

interface Nothing1
class Nothing2(val n: Nothing1)

class Nothing3(n:Nothing4)
class Nothing4(n:Nothing3)

class Test(val ting: Thing)

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