package routes

import dependencyinjection.DIContext
import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response
import routes.authentication.Authenticator

// routes.Action class that supports automatic parameter binding with Enum support
class Action(
    path: String,
    authenticationHandler: Authenticator? = null,
    private val handler: (session: IHTTPSession) -> Response
) : Route(path, authenticationHandler = authenticationHandler), RouteHandler {
    override fun getRouteHandler(diContext: DIContext): RouteHandler = this

    override fun getResponse(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handler(session)
    }

}
