package routes.authentication

import fi.iki.elonen.NanoHTTPD

abstract class Authenticator {

    abstract fun isAuthenticated(session :NanoHTTPD.IHTTPSession) :Boolean

    val requireParentAuthentication :Boolean = true

    open val authenticationFailedHandler :AuthenticationFailedHandler? = null
}

