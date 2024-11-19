package routes

import defaultDocument
import fi.iki.elonen.NanoHTTPD
import notFound

class Directory(path: String, children: List<Route> = mutableListOf()) : Route(path, children) {

    constructor(path :String, vararg children: Route) :this(path, children.toList())

    companion object {
        fun root() :Directory = Directory("")
    }

    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val default = this.defaultDocument() ?: return notFound("")
        return default.response(session)
    }
    override fun addChildren(vararg child: Route): Directory {
        return super.addChildren(*child) as Directory
    }
}