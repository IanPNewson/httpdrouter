package routes.authentication

import fi.iki.elonen.NanoHTTPD

abstract class Authenticator {

    abstract fun isAuthenticated(session :NanoHTTPD.IHTTPSession) :Boolean

    fun authenticationFailedResponse() :NanoHTTPD.Response? = null

}

