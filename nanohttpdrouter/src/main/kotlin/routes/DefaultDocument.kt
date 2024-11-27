package org.iannewson.httpdrouter.routes

import org.iannewson.httpdrouter.dependencyinjection.DIContext

class DefaultDocument(val route : Route) : Route("") {
    override fun getRouteHandler(diContext: DIContext): RouteHandler
        = route.getRouteHandler(diContext)

}