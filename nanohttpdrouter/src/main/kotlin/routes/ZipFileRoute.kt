package routes

import MimeTypes
import dependencyinjection.DIContext
import extension
import fi.iki.elonen.NanoHTTPD
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZipFileRoute(path: String, private val zip: ZipFile, private val zipEntry: ZipEntry) : Route(path), RouteHandler {
    override fun getResponse(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val extension =
            zipEntry.name.extension() ?: throw RuntimeException("Cannot serve a ZipEntry without an extension")
        val mimeType = MimeTypes[extension] ?: "application/octet-stream"
        val inputStream = zip.getInputStream(zipEntry)
        val bytes = inputStream.readAllBytes()
        return NanoHTTPD.newFixedLengthResponse(
            NanoHTTPD.Response.Status.OK,
            mimeType,
            bytes.inputStream(),
            bytes.size.toLong()
        )
    }

    override fun getRouteHandler(diContext: DIContext): RouteHandler = this
}