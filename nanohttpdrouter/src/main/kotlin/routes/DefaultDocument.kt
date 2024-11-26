import fi.iki.elonen.NanoHTTPD
import routes.Route
import routes.RouteHandler

class DefaultDocument(val route : Route) : Route("") {
    override fun getRouteHandler(diContext: DIContext): RouteHandler
        = route.getRouteHandler(diContext)

}