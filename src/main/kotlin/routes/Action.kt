package routes

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.IHTTPSession
import fi.iki.elonen.NanoHTTPD.Response
import routes.authentication.Authenticator
import text
import kotlin.reflect.KFunction
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

// routes.Action class that supports automatic parameter binding with Enum support
class Action(
    path: String,
    authenticationHandler: Authenticator? = null,
    private val handler: (session: IHTTPSession) -> Response
) : Route(path, authenticationHandler = authenticationHandler) {

    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        return handler(session)
    }

}
