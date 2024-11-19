package routes.authentication

import fi.iki.elonen.NanoHTTPD

//Denies everyone
class Deny : Authenticator() {
    override fun isAuthenticated(session: NanoHTTPD.IHTTPSession): Boolean
        = false

}

