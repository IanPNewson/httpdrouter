package routes.authentication

import fi.iki.elonen.NanoHTTPD
import redirect
import routes.Route

class RedirectAuthenticationFailedHandler(val url:String) : AuthenticationFailedHandler {
    override fun response(
        session: NanoHTTPD.IHTTPSession,
        route: Route,
        failingAuth: Authenticator
    ): NanoHTTPD.Response? = redirect(url)
}