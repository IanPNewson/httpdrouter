import org.iannewson.httpdrouter.WebApp
import org.iannewson.httpdrouter.routes.Directory
import org.iannewson.httpdrouter.routes.Router
import org.iannewson.httpdrouter.routes.addDefaultDocuments
import org.iannewson.httpdrouter.routes.postprocessing.RelativeUrlAdjusterPostProcessor

fun main() {
    val hieloRoutes =
        Directory(
            "hielo",
            Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip").children
        )
            .apply {
                addPostProcessor(RelativeUrlAdjusterPostProcessor("/hielo"))
            }
    val industriousRoutes =
        Directory(
            "industrious",
            Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-industrious\\industrious\\industrious.zip").children
        )
            .apply {
                addPostProcessor(RelativeUrlAdjusterPostProcessor("/industrious"))
            }

    val routes = Directory.root().addChildren(
        hieloRoutes,
        industriousRoutes
    )

    routes.addDefaultDocuments()

    val router = Router(routes)

    val path = router.findRoute("/hielo/index.html")!!
    val post = path.path.flatMap { it.route.collectPostProcessors() }

    val app = WebApp(router)

    Thread {
        app.start()
    }.start()

    println("Running")
    System.console().readLine()
}