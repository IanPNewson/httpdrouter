package routes

data class RoutePath(
    val route : Route,
    val rootRoute : Route,
    val path :List<RoutePathStep>)