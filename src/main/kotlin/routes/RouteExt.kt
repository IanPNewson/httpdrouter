import routes.Directory
import routes.Route

fun Directory.addDefaultDocuments(
    defaultDocuments: List<String> = listOf(
        "index.html"
    )
) {
    var defaultDocument = this.defaultDocument()
    if (defaultDocument == null) {
        for (docName in defaultDocuments) {
            val doc = this.getByPath(docName)
            if (doc != null) {
                this.addChildren(DefaultDocument(doc))
                break
            }
        }
    }

    for (subdir in this.children.filterIsInstance<Directory>())
        subdir.addDefaultDocuments(defaultDocuments)
}

fun Directory.defaultDocument(): Route? {
    return this.getByPath("")
}

fun Route.getByPath(path: String): Route? {
    return this.children.firstOrNull { it.path.equals(path, ignoreCase = true) }
}
