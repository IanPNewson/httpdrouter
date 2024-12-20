package org.iannewson.httpdrouter.routes

import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.routes.authentication.Authenticator
import org.iannewson.httpdrouter.routes.postprocessing.ResponsePostProcessor

class Directory(
    path: String,
    authenticationHandler: Authenticator? = null,
    children: MutableList<Route> = mutableListOf()
) :
    Route(path, children, authenticationHandler) {

    constructor(path: String, authenticationHandler: Authenticator? = null, vararg children: Route) : this(
        path,
        authenticationHandler,
        children.toMutableList()
    )

    constructor(path: String, children: MutableList<Route>) : this(
        path,
        null,
        children
    )

    constructor(path: String, vararg children: Route) : this(
        path,
        null,
        children.toMutableList()
    )

    companion object {
        fun root(authHandler: Authenticator? = null): Directory = Directory("", authHandler)

        fun root(vararg children: Route, authHandler: Authenticator? = null): Directory {
            return root(authHandler).addChildren(*children)
        }
    }

    override fun getRouteHandler(diContext: DIContext): RouteHandler {
        val default = this.defaultDocument() ?: return NotFoundRouteHandler()
        return default.getRouteHandler(diContext)
    }

    override fun addChildren(vararg child: Route): Directory {
        return super.addChildren(*child) as Directory
    }

    override fun collectPostProcessors(): List<ResponsePostProcessor> {
        return super.collectPostProcessors() +
                this.children.filter { it.path.isEmpty() }
                    .flatMap { it.collectPostProcessors() }
    }

}