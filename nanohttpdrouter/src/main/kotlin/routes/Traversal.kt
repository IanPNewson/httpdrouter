package org.iannewson.httpdrouter.routes

open class TRoutePath<T : Route>(route: T, rootRoute: Route, path: List<RoutePathStep>) :
    RoutePath(route, rootRoute, path) {

    @Suppress("UNCHECKED_CAST")
    override val route: T
        get() = super.route as T
}

class assets(root :Route, path :List<RoutePathStep>) : TRoutePath<Directory>(Directory("assets"), root, path)
