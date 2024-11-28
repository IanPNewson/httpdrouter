package org.iannewson.httpdrouter.responses

import com.google.gson.Gson
import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.MimeTypes
import java.io.ByteArrayInputStream

/**
 * Generates a HTTP redirect response.
 *
 * @param url The URL to redirect to.
 * @return A `NanoHTTPD.Response` configured for a 303 Redirect (See Other).
 */
fun redirect(url: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.REDIRECT_SEE_OTHER,
        "text/plain",
        "Resource redirected to $url"
    ).also {
        it.addHeader("Location", url)
    }
}

/**
 * Generates a response containing binary data.
 *
 * @param bytes The byte array to include in the response body.
 * @param mimeType The MIME type of the response content.
 * @return A `NanoHTTPD.Response` with the specified data and MIME type.
 */
fun data(bytes: ByteArray, mimeType: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.OK,
        mimeType,
        ByteArrayInputStream(bytes),
        bytes.size.toLong()
    )
}

/**
 * Generates a "Not Found" (404) response.
 *
 * @param msg The message to include in the response body.
 * @return A `NanoHTTPD.Response` configured with a 404 status.
 */
fun notFound(msg: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.NOT_FOUND,
        "text/plain",
        msg
    )
}

/**
 * Generates a "Bad Request" (400) response.
 *
 * @param msg The message to include in the response body.
 * @return A `NanoHTTPD.Response` configured with a 400 status.
 */
fun badRequest(msg: String): NanoHTTPD.Response =
    text(msg, NanoHTTPD.Response.Status.BAD_REQUEST)

/**
 * Generates an "Internal Server Error" (500) response based on an exception.
 *
 * @param ex The exception to include in the response body.
 * @return A `NanoHTTPD.Response` configured with a 500 status.
 */
fun internalError(ex: Exception): NanoHTTPD.Response {
    val str = StringBuilder()
    str.appendLine("${ex::class.simpleName}: ${ex.message}")

    for (trace in ex.stackTrace) {
        str.appendLine("${trace.className}.${trace.methodName}, line ${trace.lineNumber}")
    }

    return internalError(str.toString())
}

/**
 * Generates an "Internal Server Error" (500) response.
 *
 * @param msg The message to include in the response body.
 * @return A `NanoHTTPD.Response` configured with a 500 status.
 */
fun internalError(msg: String): NanoHTTPD.Response =
    text(msg, NanoHTTPD.Response.Status.INTERNAL_ERROR)

/**
 * Generates a plain text response.
 *
 * @param text The text to include in the response body.
 * @param httpStatus The HTTP status code for the response (default: 200 OK).
 * @return A `NanoHTTPD.Response` configured with the specified text and status.
 */
fun text(text: String, httpStatus: NanoHTTPD.Response.Status = NanoHTTPD.Response.Status.OK): NanoHTTPD.Response =
    NanoHTTPD.newFixedLengthResponse(httpStatus, MimeTypes["plain"], text)

private val gson by lazy { Gson() }

/**
 * Generates a JSON response from an object.
 *
 * @param it The object to serialize into JSON.
 * @return A `NanoHTTPD.Response` containing the serialized JSON.
 */
fun gson(it: Any): NanoHTTPD.Response {
    val str = gson.toJson(it)
    return json(str)
}

/**
 * Generates a JSON response.
 *
 * @param json The JSON string to include in the response body.
 * @return A `NanoHTTPD.Response` with the JSON content.
 */
fun json(json: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, MimeTypes["json"], json)
}

/**
 * Generates an HTML response.
 *
 * @param html The HTML content to include in the response body.
 * @return A `NanoHTTPD.Response` configured with the HTML content.
 */
fun html(html: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, MimeTypes.html, html)
}
