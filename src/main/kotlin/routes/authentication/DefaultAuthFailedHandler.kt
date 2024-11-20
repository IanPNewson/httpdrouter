package routes.authentication

import fi.iki.elonen.NanoHTTPD
import internalError
import routes.Route

class DefaultAuthFailedHandler() : AuthenticationFailedHandler {
    override fun response(
        session: NanoHTTPD.IHTTPSession,
        route: Route,
        failingAuth: Authenticator
    ): NanoHTTPD.Response? {
        return internalError("Not authenticated for $route")
    }
}