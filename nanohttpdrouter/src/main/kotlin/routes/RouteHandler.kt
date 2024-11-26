package routes

import fi.iki.elonen.NanoHTTPD

interface RouteHandler {
    fun getResponse(session: NanoHTTPD.IHTTPSession) : NanoHTTPD.Response
}

