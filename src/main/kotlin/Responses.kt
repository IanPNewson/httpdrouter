import fi.iki.elonen.NanoHTTPD
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

fun redirect(url: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.REDIRECT_SEE_OTHER,
        "text/plain",
        "Resource redirected to $url"
    )
        .also {
            it.addHeader("Location", url)
        }
}

//fun bitmap(bitmap: Bitmap, quality: Int = 100): NanoHTTPD.Response {
//    val stream = ByteArrayOutputStream()
//    bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream)
//    val bytes = stream.toByteArray()
//    return data(bytes, "image/png")
//}

fun data(bytes: ByteArray, mimeType: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.OK,
        mimeType,
        ByteArrayInputStream(bytes),
        bytes.size.toLong()
    )
}

//fun resourceData(resourceId: Int, mimeType: String): NanoHTTPD.Response {
//    val bytes = this.ctx.resources.openRawResource(resourceId).readAllBytes()
//
//    val _test = String(bytes, StandardCharsets.UTF_8);
//
//    return data(bytes, mimeType)
//}

//fun staticFileData(file :routes.StaticFile) : NanoHTTPD.Response {
//    return resourceData(file.resourceId, file.mimeType)
//}

fun notFound(msg: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(
        NanoHTTPD.Response.Status.NOT_FOUND,
        "text/plain",
        msg
    )
}

private fun badRequest(msg: String) =
    text(msg, NanoHTTPD.Response.Status.BAD_REQUEST)

fun internalError(ex: Exception): NanoHTTPD.Response {
    val str = StringBuilder()
    str.appendLine(ex::class.simpleName + ": " + ex.message)

    for (trace in ex.stackTrace) {
        str.appendLine("${trace.className}.${trace.methodName}, line ${trace.lineNumber}")
    }

    return internalError(str.toString())
}

fun internalError(msg :String): NanoHTTPD.Response =
    text(msg, NanoHTTPD.Response.Status.INTERNAL_ERROR)

fun text(text: String, httpStatus: NanoHTTPD.Response.Status = NanoHTTPD.Response.Status.OK) =
    NanoHTTPD.newFixedLengthResponse(httpStatus, "text/plain", text)

//private val gson by lazy { Gson() }
//
//fun json(it: Any): NanoHTTPD.Response {
//    val str = gson.toJson(it)
//    return json(str)
//}

fun json(json: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json", json)
}

fun html(html: String): NanoHTTPD.Response {
    return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "text/html", html)
}

//fun html(resourceId: Int): NanoHTTPD.Response {
//    val html = this.ctx.readRawResource(resourceId)
//    return html(html)
//}
