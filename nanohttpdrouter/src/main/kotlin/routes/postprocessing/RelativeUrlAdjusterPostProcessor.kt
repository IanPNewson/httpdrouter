import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.routes.postprocessing.JsoupHtmlPostProcessor
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class RelativeUrlAdjusterPostProcessor(private val subfolder: String) : JsoupHtmlPostProcessor() {

    override fun processDocument(document: Document, session: NanoHTTPD.IHTTPSession): Document {
        // Ensure subfolder has no leading or trailing slashes
        val normalizedSubfolder = subfolder.trim('/')

        // Adjust all <a>, <img>, <link>, and <script> elements
        document.select("a[href], img[src], link[href], script[src]").forEach { element ->
            val attribute = if (element.hasAttr("href")) "href" else "src"
            adjustUrl(element, attribute, normalizedSubfolder)
        }

        return document
    }

    private fun adjustUrl(element: Element, attribute: String, subfolder: String) {
        val url = element.attr(attribute)

        // Ignore absolute URLs (e.g., starting with "http://" or "/")
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) {
            return
        }

        // Adjust relative URL by prepending the subfolder
        val adjustedUrl = "$subfolder/$url".replace("//", "/")
        element.attr(attribute, adjustedUrl)
    }
}
