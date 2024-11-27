package org.iannewson.httpdrouter.routes.authentication

import fi.iki.elonen.NanoHTTPD

class AllowIf(val func :(session : NanoHTTPD.IHTTPSession) -> Boolean) : Authenticator() {
    override fun isAuthenticated(session: NanoHTTPD.IHTTPSession): Boolean = func(session)
}