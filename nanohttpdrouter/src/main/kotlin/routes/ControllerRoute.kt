package org.iannewson.httpdrouter.routes

import org.iannewson.httpdrouter.controllers.Controller
import org.iannewson.httpdrouter.dependencyinjection.DIContext

class ControllerRoute<T : Controller>(
    path: String,
    val clazz: Class<T>
) : Route(path) {

    fun instantiateController(diContext: DIContext): T {
        return diContext.get(clazz) as T
    }

    override fun getRouteHandler(diContext: DIContext): RouteHandler = instantiateController(diContext)

}