package org.iannewson.httpdrouter.routes

data class RoutePath(
    val route : Route,
    val rootRoute : Route,
    val path :List<RoutePathStep>) {

    fun fullPath(): String {
        return path.joinToString(separator = "/") { it.route.path }
    }

    fun withChild(child: Route, childIndex: Int): RoutePath {
        // Create a new path by appending the child route and its index
        val newPath = this.path.toMutableList()
        newPath.add(RoutePathStep(child, childIndex))
        return RoutePath(
            route = child,
            rootRoute = this.rootRoute,
            path = newPath
        )
    }


}