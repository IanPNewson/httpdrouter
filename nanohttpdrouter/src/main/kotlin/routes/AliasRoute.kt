package org.iannewson.httpdrouter.routes

import org.iannewson.httpdrouter.dependencyinjection.DIContext

class AliasRoute(path :String, val target :Route) : Route(path) {
    override fun getRouteHandler(diContext: DIContext): RouteHandler
        = target.getRouteHandler(diContext)

}