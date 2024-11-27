package org.iannewson.httpdrouter.routes.authentication

import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.responses.internalError
import org.iannewson.httpdrouter.routes.Route

class DefaultAuthFailedHandler() : AuthenticationFailedHandler {
    override fun response(
        session: NanoHTTPD.IHTTPSession,
        route: Route,
        failingAuth: Authenticator
    ): NanoHTTPD.Response? {
        return internalError("Not authenticated for $route")
    }
}