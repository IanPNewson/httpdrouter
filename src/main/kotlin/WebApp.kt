import fi.iki.elonen.NanoHTTPD
import routes.Router

class WebApp(val router: Router) : NanoHTTPD(81) {

    override fun serve(_session: IHTTPSession?): Response {

        val session =_session?:return internalError("No session provided")

        try {
            val route = router.findRoute(session.uri) ?: return notFound("No route found for ${session.uri}")
            val response = route.response(session)
            return response
        } catch (ex :Exception) {
            return internalError(ex)
        }
    }

}

