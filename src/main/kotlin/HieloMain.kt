import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.WebApp
import org.iannewson.httpdrouter.routes.Directory
import org.iannewson.httpdrouter.routes.Router
import org.iannewson.httpdrouter.routes.addDefaultDocuments
import org.jsoup.nodes.Document

fun main() {
    val routes = Router.createRouteTreeFromZip("C:\\Users\\Ian\\Downloads\\templated-hielo\\hielo.zip")
    routes.addDefaultDocuments()
//    routes.postProcessors.add(object : JsoupHtmlPostProcessor() {
//        override fun processDocument(document: Document, session: NanoHTTPD.IHTTPSession): Document {
//            // Add a header to the body
//            document.head().appendChild(document.createElement("base").also {
//                it.attributes().add("href", "/hielo/")
//            })
//            return document
//        }
//    })

    val app = WebApp(routes, 1024)

    Thread {
        app.start()
    }.start()

    println("Running")
    System.console().readLine()
}