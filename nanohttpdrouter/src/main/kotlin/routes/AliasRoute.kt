package org.iannewson.httpdrouter.routes

import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.routes.postprocessing.ResponsePostProcessor

open class AliasRoute(path :String, val target :Route) : Route(path) {
    override fun getRouteHandler(diContext: DIContext): RouteHandler
        = target.getRouteHandler(diContext)

    override fun collectPostProcessors(): List<ResponsePostProcessor> {
        return super.collectPostProcessors() + this.target.collectPostProcessors()
    }

}