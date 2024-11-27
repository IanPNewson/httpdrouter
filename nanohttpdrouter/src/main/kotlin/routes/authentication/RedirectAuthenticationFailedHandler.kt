package org.iannewson.httpdrouter.routes.authentication

import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.responses.redirect
import org.iannewson.httpdrouter.routes.Route

class RedirectAuthenticationFailedHandler(val url:String) : AuthenticationFailedHandler {
    override fun response(
        session: NanoHTTPD.IHTTPSession,
        route: Route,
        failingAuth: Authenticator
    ): NanoHTTPD.Response? = redirect(url)
}