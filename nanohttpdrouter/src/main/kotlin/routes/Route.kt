package routes

import DefaultDocument
import MimeTypes
import extension
import fi.iki.elonen.NanoHTTPD
import routes.authentication.Authenticator

// Base Route class and its subclasses for different route types
abstract class Route(val path: String,
                     val children: List<Route> = mutableListOf(),
                     val authenticationHandler : Authenticator? = null) {

    init {
        if (path.lastIndexOf("/") > 0) {
            throw RuntimeException("Route paths should all be relative and therefore shouldn't contain / (unless it's the first character)")
        }
    }



    abstract fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response

    open val extension: String?
        get() = this.path.extension()

    val mimeType: String?
        get() {
            val ext = extension ?: return null
            return MimeTypes[ext]
        }

    open fun addChildren(vararg child: Route): Route {
        (this.children as MutableList<Route>).addAll(child)
        return this
    }

    override fun toString(): String {
        return describeRouteTree(this)
    }

    companion object {

        fun describeRouteTree(route: Route): String {
            val str = java.lang.StringBuilder()
            describeRouteTree(route, "", str)
            return str.toString()
        }

        fun describeRouteTree(route: Route, indent: String, builder: StringBuilder = java.lang.StringBuilder()) {

            if (route is DefaultDocument) {
                builder.appendLine("$indent${route::class.simpleName}: ${route.route.path}")
            } else {
                builder.appendLine("$indent${route::class.simpleName}: ${route.path}")
            }

            route.children.forEach { child ->
                describeRouteTree(child, "$indent  ", builder)
            }
        }

    }

}


