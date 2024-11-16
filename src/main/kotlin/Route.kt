import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response
import java.nio.file.Path
import kotlin.io.path.readBytes

// Base Route class and its subclasses for different route types
sealed class Route(val path: String, val children: List<Route> = mutableListOf()) {
    abstract fun response(session: NanoHTTPD.IHTTPSession) :Response

    open val extension :String?
        get() = this.path.extension()

    val mimeType : String?
        get() {
            val ext = extension ?: return null
            return MimeTypes[ext]
        }

}

class Directory(path: String, children: List<Route>) : Route(path, children) {
    override fun response(session: NanoHTTPD.IHTTPSession) :Response {
        TODO("Not yet implemented")
    }
}

class StaticFile(path: String, val resourcePath :String) : Route(path) {
    override fun response(session: NanoHTTPD.IHTTPSession): Response {
        val bytes = Path.of(resourcePath)
            .readBytes()
        return data(bytes, mimeType!!)
    }

    override val extension: String?
        get() {
            val value = super.extension
            if (value.isNullOrEmpty()) return this.resourcePath.extension()
            return value
        }
}

