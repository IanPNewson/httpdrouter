package org.iannewson.httpdrouter.routes.postprocessing

import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.MimeTypes

class HtmlManipulationPostProcessor : ResponsePostProcessor {
    override fun process(response: NanoHTTPD.Response, session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        if (response.mimeType == MimeTypes.html) {
            val modifiedContent = response.data.bufferedReader().use { it.readText() }
                .replace("<body>", "<body><h1>Injected Header</h1>")

            return NanoHTTPD.newFixedLengthResponse(response.status, response.mimeType, modifiedContent)
        }
        return response
    }
}

