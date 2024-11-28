import fi.iki.elonen.NanoHTTPD
import org.iannewson.httpdrouter.routes.postprocessing.JsoupHtmlPostProcessor
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * A post-processor that adjusts relative URLs in HTML documents to include a specified subfolder.
 *
 * This post-processor is useful for serving content from a subfolder by dynamically updating
 * relative URLs in `<a>`, `<img>`, `<link>`, and `<script>` elements to include the subfolder path.
 *
 * Example:
 * - Given a subfolder `subdir` and an element `<img src="image.png">`, the `src` will be adjusted to `subdir/image.png`.
 *
 * Absolute URLs (e.g., `http://example.com` or `/absolute/path`) are left unchanged.
 *
 * @property subfolder The subfolder to prepend to relative URLs. Trailing or leading slashes will be trimmed.
 */
class RelativeUrlAdjusterPostProcessor(private val subfolder: String) : JsoupHtmlPostProcessor() {

    /**
     * Processes the provided HTML document by adjusting all relevant URLs to include the subfolder.
     *
     * This method targets `<a href>`, `<img src>`, `<link href>`, and `<script src>` attributes and
     * modifies them if they contain relative URLs.
     *
     * @param document The HTML document to process.
     * @param session The HTTP session associated with the request (not directly used here).
     * @return The modified HTML document.
     */
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

    /**
     * Adjusts a single element's URL attribute to include the subfolder if it's a relative URL.
     *
     * @param element The HTML element to adjust.
     * @param attribute The attribute (`href` or `src`) containing the URL to adjust.
     * @param subfolder The subfolder to prepend to the relative URL.
     */
    private fun adjustUrl(element: Element, attribute: String, subfolder: String) {
        val url = element.attr(attribute)

        // Ignore absolute URLs (e.g., starting with "http://", "https://", or "/")
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) {
            return
        }

        // Adjust relative URL by prepending the subfolder
        val adjustedUrl = "$subfolder/$url".replace("//", "/")
        element.attr(attribute, adjustedUrl)
    }
}
