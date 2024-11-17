package routes

import MimeTypes
import extension
import fi.iki.elonen.NanoHTTPD
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ZipFileRoute(path: String, private val zipFilePath: Path, private val zipEntry: ZipEntry) : Route(path) {
    override fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val extension = zipEntry.name.extension() ?: throw RuntimeException("Cannot serve a ZipEntry without an extension")
        val mimeType = MimeTypes[extension] ?: "application/octet-stream"
        ZipFile(zipFilePath.toFile()).use { zip ->
            val inputStream = zip.getInputStream(zipEntry)
            val bytes = inputStream.readAllBytes()
            return NanoHTTPD.newFixedLengthResponse(
                NanoHTTPD.Response.Status.OK,
                mimeType,
                bytes.inputStream(),
                bytes.size.toLong()
            )
        }
    }
}