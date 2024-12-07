package org.iannewson.httpdrouter.routes.builder

import org.iannewson.httpdrouter.routes.Action
import org.iannewson.httpdrouter.routes.Directory
import org.iannewson.httpdrouter.routes.Route
import org.iannewson.httpdrouter.routes.authentication.Authenticator

class RouteBuilder(
    var path: String,
    var authenticationHandler: Authenticator? = null,
    val children: MutableList<RouteBuilder> = mutableListOf()
) {

    fun child(init: RouteBuilder.() -> Unit) {
        val childBuilder = RouteBuilder(path = "")
        childBuilder.init()
        children.add(childBuilder)
    }

    fun build(): Route {
        val builtChildren = children.map { it.build() }
        return when {
            builtChildren.isNotEmpty() -> Directory(path, authenticationHandler, builtChildren.toMutableList())
            else -> Action(path, authenticationHandler) { session -> TODO() }
        }
    }
}

fun routeTree(init: RouteBuilder.() -> Unit): Route {
    val rootBuilder = RouteBuilder(path = "")
    rootBuilder.init()
    return rootBuilder.build()
}

fun Route.builder(): RouteBuilder {
    val builder = RouteBuilder(path, authenticationHandler)
    builder.children.addAll(children.map { it.builder() })
    return builder
}

fun Route.modify(init: RouteBuilder.() -> Unit): Route {
    val builder = this.builder()
    builder.init()
    return builder.build()
}
