package routes

import controllers.Controller
import dependencyinjection.DIContext
import fi.iki.elonen.NanoHTTPD

class ControllerRoute<T :Controller>(
    path: String,
    val clazz: Class<T>
) : Route(path) {

    fun instantiateController(diContext: DIContext): T {
        return diContext.get(clazz) as T
    }

    override fun getRouteHandler(diContext: DIContext): RouteHandler = instantiateController(diContext)

}