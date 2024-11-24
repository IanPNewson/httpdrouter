import routes.Directory
import routes.Route
import routes.StaticFile

fun Directory.addDefaultDocuments(
    defaultDocuments: List<String> = listOf(
        "index.html",
        "home.html"
    )
) {
    val defaultDocument = this.defaultDocument()
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

fun Route.merge(additional: Route): Route {
    if (this.path != additional.path) {
        throw IllegalArgumentException("Cannot merge routes with different root paths: '${this.path}' and '${additional.path}'")
    }

    return when {
        this is Directory && additional is Directory -> {
            // Merge directories
            val mergedChildren = mutableMapOf<String, Route>()

            // Add all children from the base directory
            this.children.forEach { child ->
                mergedChildren[child.path] = child
            }

            // Add or merge children from the additional directory
            additional.children.forEach { child ->
                val existing = mergedChildren[child.path]
                if (existing != null) {
                    // Recursively merge if a child with the same path exists
                    mergedChildren[child.path] = existing.merge( child)
                } else {
                    // Add the new child
                    mergedChildren[child.path] = child
                }
            }

            // Return a new merged Directory
            Directory(this.path, mergedChildren.values.toMutableList())
        }

        this is StaticFile && additional is StaticFile -> {
            // Conflict: Two StaticFiles with the same path
            if (this.resourcePath != additional.resourcePath) {
                throw IllegalArgumentException("Conflict: Two StaticFiles with the same path '${this.path}' but different resources.")
            }
            // If they're the same file, return one of them
            this
        }

        else -> {
            // Conflict: One is a Directory and the other is a StaticFile
            throw IllegalArgumentException("Conflict: '${this.path}' exists as both a Directory and a StaticFile.")
        }
    }
}