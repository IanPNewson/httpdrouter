package controllers

import fi.iki.elonen.NanoHTTPD
import routes.RouteHandler

abstract class Controller : RouteHandler {
    fun handleRequest(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        TODO()
    }

}