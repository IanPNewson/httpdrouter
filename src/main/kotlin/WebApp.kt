import fi.iki.elonen.NanoHTTPD
import routes.Router
import routes.authentication.AuthenticationFailedException

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

                    throw AuthenticationFailedException(route, authenticationHandler)
                }
            }

            val response = route.response(session)
            return response
        } catch (ex :Exception) {
            return internalError(ex)
        }
    }

}

