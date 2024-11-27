package org.iannewson.httpdrouter

import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.responses.internalError
import org.iannewson.httpdrouter.responses.notFound
import org.iannewson.httpdrouter.routes.Route
import org.iannewson.httpdrouter.routes.Router
import org.iannewson.httpdrouter.routes.authentication.AuthenticationFailedException

class WebApp(
    val router: Router,
    val diContext: DIContext = DIContext()
) : NanoHTTPD(81) {

    constructor(routes :Route, diContext: DIContext = DIContext()) :
            this(Router(routes), diContext)

    override fun serve(_session: IHTTPSession?): Response {

        val session = _session ?: return internalError("No session provided")

        try {
            val routePath = router.findRoute(session.uri) ?: return notFound("No route found for ${session.uri}")

            val authHandlers =
                routePath.path.map { it.route.authenticationHandler }
                    .filterNotNull()
                    .reversed()

            for (authenticationHandler in authHandlers) {
                if (!authenticationHandler.isAuthenticated(session)) {
                    val authFailHandler = authenticationHandler.authenticationFailedHandler ?: router.defaultAuthFailedHandler
                    val authFailureResponse = authFailHandler?.response(
                        session,
                        routePath.route,
                        authenticationHandler
                    )

                    return authFailureResponse ?:
                        throw AuthenticationFailedException(routePath.route, authenticationHandler)
                }
            }

            val route = routePath.route
            val routeHandler = route.getRouteHandler(diContext)

            val response = routeHandler.getResponse(session)
            return response
        } catch (ex: Exception) {
            return internalError(ex)
        }
    }

}

