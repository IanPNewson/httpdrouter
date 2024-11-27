package org.iannewson.httpdrouter.routes.authentication

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response
import org.iannewson.httpdrouter.routes.Route

interface AuthenticationFailedHandler {

    fun response(session : NanoHTTPD.IHTTPSession,
                 route : Route,
                 failingAuth : Authenticator
    ) :Response?

}

