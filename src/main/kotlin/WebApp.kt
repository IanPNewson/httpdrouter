import fi.iki.elonen.NanoHTTPD
import routes.Route

class WebApp(routes: Route) : NanoHTTPD(81) {

    private val router = Router(routes)

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

