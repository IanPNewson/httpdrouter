package org.iannewson.httpdrouter.routes

import org.iannewson.httpdrouter.MimeTypes
import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.extension
import org.iannewson.httpdrouter.routes.authentication.Authenticator
import org.iannewson.httpdrouter.routes.postprocessing.ResponsePostProcessor

// Base Route class and its subclasses for different route types
abstract class Route(val path: String,
                     val children: MutableList<Route> = mutableListOf(),
                     val authenticationHandler : Authenticator? = null,
                     private val postProcessors: MutableList<ResponsePostProcessor> = mutableListOf()) {

    init {
        if (path.indexOf("/") > -1) {
            throw RuntimeException("Route paths should all be relative and therefore shouldn't contain / (unless it's the first character)")
        }
    }

    abstract fun getRouteHandler(diContext: DIContext):RouteHandler

    open val extension: String?
        get() = this.path.extension()

    val mimeType: String?
        get() {
            val ext = extension ?: return null
            return MimeTypes[ext]
        }

    open fun addChildren(vararg child: Route): Route {
        this.children.addAll(child)
        return this
    }

    fun addPostProcessor(postProcessor: ResponsePostProcessor) :Route {
        this.postProcessors.add(postProcessor)
        return this
    }

    open fun collectPostProcessors() :List<ResponsePostProcessor> = postProcessors

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
                builder.appendLine("$indent${route::class.simpleName}: ${route.target.path}")
            } else {
                builder.appendLine("$indent${route::class.simpleName}: ${route.path}")
            }

            route.children.forEach { child ->
                describeRouteTree(child, "$indent  ", builder)
            }
        }

    }

}

