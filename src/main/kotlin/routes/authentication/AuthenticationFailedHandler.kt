package routes.authentication

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response
import routes.Route

interface AuthenticationFailedHandler {

    fun response(session : NanoHTTPD.IHTTPSession,
                 route :Route,
                 failingAuth :Authenticator) :Response?

}

