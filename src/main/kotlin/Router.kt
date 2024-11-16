class Router(val rootRoute :Route) {

    fun findRoute(path: String?): Route? {
        if (path == null) return null
        val pathParts = path.trim('/').split('/')
        return findRouteRecursively((rootRoute as Directory).children, pathParts)
    }

    private fun findRouteRecursively(routes: List<Route>, pathParts: List<String>): Route? {
        if (pathParts.isEmpty()) return null
        val currentPart = pathParts.first()

        val matchedRoute = routes.firstOrNull { it.path == currentPart }
        return when (matchedRoute) {
            is Directory -> findRouteRecursively(matchedRoute.children, pathParts.drop(1))
            is StaticFile/*, is Action*/ -> if (pathParts.size == 1) matchedRoute else null
            else -> null
        }
    }
}