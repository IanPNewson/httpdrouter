import org.iannewson.httpdrouter.WebApp
import org.iannewson.httpdrouter.routes.Directory
import org.iannewson.httpdrouter.routes.Router
import org.iannewson.httpdrouter.routes.addDefaultDocuments

fun main() {
    val routes = Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")
    routes.addDefaultDocuments()
    val app = WebApp(routes)

    Thread {
        app.start()
    }.start()

    println("Running")
    System.console().readLine()
}