package routes

import data
import extension
import fi.iki.elonen.NanoHTTPD
import notFound
import java.nio.file.Path
import kotlin.io.path.readBytes

open class StaticFile(path: String, val resourcePath: String) : Route(path) {
    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
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

    override val extension: String?
        get() {
            val value = super.extension
            if (value.isNullOrEmpty()) return this.resourcePath.extension()
            return value
        }
}