package routes

import fi.iki.elonen.NanoHTTPD

class AliasRoute(path :String, val target :Route) : Route(path) {
    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return target.response(session)
    }
}