package org.iannewson.httpdrouter.routes

data class RouteValidationError(
    val message: String,          // A descriptive error message explaining the issue
    val routePath: RoutePath      // The path in the route tree where the error occurred
) {
    override fun toString(): String {
        return "RouteValidationError(message='$message', path='${routePath.path.joinToString(" -> ") { it.route.path }}')"
    }
}