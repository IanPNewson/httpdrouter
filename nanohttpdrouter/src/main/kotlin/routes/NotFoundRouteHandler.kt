package org.iannewson.httpdrouter.routes

import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.responses.notFound

class NotFoundRouteHandler : RouteHandler {
    override fun getResponse(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return notFound("${session.uri} not found on this server")
    }
}