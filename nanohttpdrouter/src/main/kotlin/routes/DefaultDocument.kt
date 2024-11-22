import fi.iki.elonen.NanoHTTPD
import routes.Route

class DefaultDocument(val route : Route) : Route("") {
    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return route.response(session)
    }

}