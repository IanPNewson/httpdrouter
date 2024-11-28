package org.iannewson.httpdrouter.routes.postprocessing

import fi.iki.elonen.NanoHTTPD
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

abstract class JsoupHtmlPostProcessor : ResponsePostProcessor {
    /**
     * Process the HTML content using Jsoup.
     *
     * @param document The parsed Jsoup Document for structured HTML manipulation.
     * @param session The HTTP session of the request.
     * @return The modified Jsoup Document.
     */
    abstract fun processDocument(document: Document, session: NanoHTTPD.IHTTPSession): Document

    override fun process(response: NanoHTTPD.Response, session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        // Only process responses with HTML content
        if (response.mimeType != "text/html") {
            return response
        }

        // Parse the HTML content with Jsoup
        val originalHtml = response.data.bufferedReader().use { it.readText() }
        val document = Jsoup.parse(originalHtml)

        // Let the subclass manipulate the document
        val modifiedDocument = processDocument(document, session)

        // Serialize the modified document back to a string
        val modifiedHtml = modifiedDocument.html()

        // Return a new response with the modified HTML
        return NanoHTTPD.newFixedLengthResponse(response.status, response.mimeType, modifiedHtml)
    }
}
