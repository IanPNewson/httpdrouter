package org.iannewson.httpdrouter

object MimeTypes {
    val fileExtensionToMimeType = mapOf(

        "html" to "text/html",
        "htm" to "text/html",
        "css" to "text/css",
        "js" to "application/javascript",
        "json" to "application/json",
        "xml" to "application/xml",
        "txt" to "text/plain",
        "jpg" to "image/jpeg",
        "jpeg" to "image/jpeg",
        "png" to "image/png",
        "gif" to "image/gif",
        "svg" to "image/svg+xml",
        "ico" to "image/vnd.microsoft.icon",
        "bmp" to "image/bmp",
        "webp" to "image/webp",
        "mp3" to "audio/mpeg",
        "wav" to "audio/wav",
        "ogg" to "audio/ogg",
        "mp4" to "video/mp4",
        "webm" to "video/webm",
        "avi" to "video/x-msvideo",
        "pdf" to "application/pdf",
        "zip" to "application/zip",
        "rar" to "application/vnd.rar",
        "7z" to "application/x-7z-compressed",
        "tar" to "application/x-tar",
        "gz" to "application/gzip",
        "doc" to "application/msword",
        "docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "xls" to "application/vnd.ms-excel",
        "xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "ppt" to "application/vnd.ms-powerpoint",
        "pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    )

    fun getMimeType(extension: String): String? {
        return fileExtensionToMimeType[extension]
    }

    operator fun get(extension: String): String? {
        return getMimeType(extension)
    }

}