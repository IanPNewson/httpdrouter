import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response
import java.nio.file.Path
import kotlin.io.path.readBytes

// Base Route class and its subclasses for different route types
abstract class Route(val path: String, val children: List<Route> = mutableListOf()) {

    init {
        if (path.lastIndexOf("/") > 0) {
            throw RuntimeException("Route paths should all be relative and therefore shouldn't contain / (unless it's the first character)")
        }
    }

    abstract fun response(session: NanoHTTPD.IHTTPSession) :Response

    open val extension :String?
        get() = this.path.extension()

    val mimeType : String?
        get() {
            val ext = extension ?: return null
            return MimeTypes[ext]
        }

    fun addChildren(vararg child: Route) : Route {
        (this.children as MutableList<Route>).addAll(child)
        return this
    }

}

class StaticFile(path: String, val resourcePath :String) : Route(path) {
    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val mimeType = mimeType ?: return notFound("Can't provide a response for $path as there is no supported  mime type")

        val bytes = Path.of(resourcePath)
            .readBytes()
        return data(bytes, mimeType)
    }

    override val extension: String?
        get() {
            val value = super.extension
            if (value.isNullOrEmpty()) return this.resourcePath.extension()
            return value
        }
}

class Directory(path: String, children: List<Route> = mutableListOf()) : Route(path, children) {
    override fun response(session: NanoHTTPD.IHTTPSession) : NanoHTTPD.Response {
        TODO("Directories don't support a response")
    }
}
