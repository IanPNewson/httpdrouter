package routes

import defaultDocument
import fi.iki.elonen.NanoHTTPD
import notFound
import routes.authentication.Authenticator

class Directory(path: String, authenticationHandler: Authenticator? = null, children: List<Route> = mutableListOf()) :
    Route(path, children, authenticationHandler) {

    constructor(path: String, authenticationHandler: Authenticator? = null, vararg children: Route) : this(
        path,
        authenticationHandler,
        children.toList()
    )

    constructor(path: String, children: List<Route>) : this(
        path,
        null,
        children
    )

    constructor(path: String, vararg children: Route) : this(
        path,
        null,
        children.toList()
    )

    companion object {
        fun root(authHandler : Authenticator? = null): Directory = Directory("", authHandler)

        fun root(vararg children: Route, authHandler : Authenticator? = null) : Directory {
            return root(authHandler).addChildren(*children)
        }
    }

    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val default = this.defaultDocument() ?: return notFound("")
        return default.response(session)
    }

    override fun addChildren(vararg child: Route): Directory {
        return super.addChildren(*child) as Directory
    }
}