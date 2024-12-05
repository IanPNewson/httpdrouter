package org.iannewson.httpdrouter

import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.responses.internalError
import org.iannewson.httpdrouter.responses.notFound
import org.iannewson.httpdrouter.routes.Route
import org.iannewson.httpdrouter.routes.Router
import org.iannewson.httpdrouter.routes.authentication.AuthenticationFailedException

open class WebApp(
    val router: Router,
    val diContext: DIContext = createDefaultDIContext(),
    val port: Int = PORT_DEFAULT
) : NanoHTTPD(port) {

    companion object {
        const val PORT_DEFAULT = 80

        fun createDefaultDIContext() = DIContext()
    }

    constructor(
        routes: Route,
        diContext: DIContext = createDefaultDIContext(),
        port: Int = PORT_DEFAULT
    ) : this(Router(routes), diContext, port)

    override fun serve(session: IHTTPSession?): Response {
        if (session == null) return internalError("No session provided")

        try {
            val routePath = router.findRoute(session.uri) ?: return notFound("No route found for ${session.uri}")

            val authHandlers =
                routePath.path.mapNotNull { it.route.authenticationHandler }
                    .reversed()

            for (authenticationHandler in authHandlers) {
                if (!authenticationHandler.isAuthenticated(session)) {
                    val authFailHandler =
                        authenticationHandler.authenticationFailedHandler ?: router.defaultAuthFailedHandler
                    val authFailureResponse = authFailHandler?.response(
                        session,
                        routePath.route,
                        authenticationHandler
                    )

                    return authFailureResponse ?: throw AuthenticationFailedException(
                        routePath.route,
                        authenticationHandler
                    )
                }
            }

            val route = routePath.route
            val routeHandler = route.getRouteHandler(diContext)

            var response = routeHandler.getResponse(session)
            // Collect and apply all post-processors from the route tree
            var postProcessors = routePath.path.flatMap { it.route.collectPostProcessors() }
            postProcessors += router.rootRoute.collectPostProcessors()
            for (postProcessor in postProcessors) {
                response = postProcessor.process(response, session)
            }

            return response
        } catch (ex: Exception) {
            return internalError(ex)
        }
    }

}

