package routes

import fi.iki.elonen.NanoHTTPD

class Directory(path: String, children: List<Route> = mutableListOf()) : Route(path, children) {
    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        TODO("Directories don't support a response")
    }
}