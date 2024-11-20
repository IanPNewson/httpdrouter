import fi.iki.elonen.NanoHTTPD
import routes.Route
import routes.Router
import routes.authentication.AuthenticationFailedException
import routes.authentication.AuthenticationFailedHandler
import routes.authentication.Authenticator

class WebApp(val router: Router) : NanoHTTPD(81) {

    override fun serve(_session: IHTTPSession?): Response {

        val session =_session?:return internalError("No session provided")

        try {
            val route = router.findRoute(session.uri) ?: return notFound("No route found for ${session.uri}")

            val routePath = router.findRoutePath(route)

            val authHandlers =
                routePath.path.map { it.route.authenticationHandler }
                .filterNotNull()
                .reversed()

            for (authenticationHandler in authHandlers) {
                if (!authenticationHandler.isAuthenticated(session)) {
//                    val authFailedHandler = authenticationHandler.authenticationFailedHandler
//                        ?:

//                    val authFailureResponse = authenticationHandler.response(authFailedHandler)
                    throw AuthenticationFailedException(route, authenticationHandler)
                } else if (authenticationHandler.requireParentAuthentication) {

                }

            }

            val response = route.response(session)
            return response
        } catch (ex :Exception) {
            return internalError(ex)
        }
    }

}

class DefaultAuthFailedHandler() :AuthenticationFailedHandler {
    override fun response(
        session: NanoHTTPD.IHTTPSession,
        route: Route,
        failingAuth: Authenticator
    ): NanoHTTPD.Response? {
        return text("Not authenticated for $route")
    }
}