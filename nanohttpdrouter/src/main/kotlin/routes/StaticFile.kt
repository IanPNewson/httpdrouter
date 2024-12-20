package org.iannewson.httpdrouter.routes

import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.extension
import org.iannewson.httpdrouter.responses.data
import org.iannewson.httpdrouter.responses.notFound
import java.nio.file.Path
import kotlin.io.path.readBytes

open class StaticFile(path: String, val resourcePath: String) : Route(path), RouteHandler {
    override fun getResponse(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val mimeType =
            mimeType ?: return notFound("Can't provide a response for $path as there is no supported  mime type")

        val bytes = readBytes()
        return data(bytes, mimeType)
    }

    protected fun readBytes(): ByteArray {
        val bytes = Path.of(resourcePath)
            .readBytes()
        return bytes
    }

    override fun getRouteHandler(diContext: DIContext): RouteHandler = this

    override val extension: String?
        get() {
            val value = super.extension
            if (value.isNullOrEmpty()) return this.resourcePath.extension()
            return value
        }
}