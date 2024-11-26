package routes

import dependencyinjection.DIContext
import fi.iki.elonen.NanoHTTPD

class AliasRoute(path :String, val target :Route) : Route(path) {
    override fun getRouteHandler(diContext: DIContext): RouteHandler
        = target.getRouteHandler(diContext)

}