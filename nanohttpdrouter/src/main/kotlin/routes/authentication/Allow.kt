package routes.authentication

import fi.iki.elonen.NanoHTTPD

class Allow : Authenticator() {
    override fun isAuthenticated(session: NanoHTTPD.IHTTPSession): Boolean
        = true
}