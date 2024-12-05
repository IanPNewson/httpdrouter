package org.iannewson.httpdrouter.routes

/**
 * Extension function to add default documents to a directory.
 * A default document is a file like "index.html" or "home.html" that acts as the
 * entry point for the directory. This function applies recursively to subdirectories.
 *
 * @param defaultDocuments List of default document names to check (e.g., ["index.html", "home.html"]).
 */
fun Directory.addDefaultDocuments(
    defaultDocuments: List<String> = listOf(
        "index.html",
        "home.html"
    )
) {
    // Check if this directory already has a default document
    val defaultDocument = this.defaultDocument()
    if (defaultDocument == null) {
        // If no default document exists, look for one in the directory's children
        for (docName in defaultDocuments) {
            val doc = this.getByPath(docName)
            if (doc != null) {
                // Add the first matching default document as a DefaultDocument route
                this.addChildren(DefaultDocument(doc))
                break
            }
        }
    }

    // Recursively apply default document logic to child directories
    for (subdir in this.children.filterIsInstance<Directory>())
        subdir.addDefaultDocuments(defaultDocuments)
}

/**
 * Checks if this directory has a default document (identified by an empty path "").
 *
 * @return The default document route, or null if none exists.
 */
fun Directory.defaultDocument(): Route? {
    return this.getByPath("")
}

/**
 * Finds a child route by its path.
 *
 * @param path The relative path of the route to search for.
 * @return The matching route, or null if no route with the specified path exists.
 */
fun Route.getByPath(path: String): Route? {
    return this.children.firstOrNull { it.path.equals(path, ignoreCase = true) }
}

/**
 * Merges another route tree into this route tree.
 *
 * @param additional The route tree to merge into this one.
 * @return The merged route tree.
 * @throws IllegalArgumentException if the root paths of the two routes are different,
 * or if there are conflicts (e.g., duplicate StaticFiles with different resources).
 */
fun Route.merge(additional: Route): Route {
    if (this.path != additional.path) {
        throw IllegalArgumentException("Cannot merge routes with different root paths: '${this.path}' and '${additional.path}'")
    }

    return when {
        // Both routes are directories
        this is Directory && additional is Directory -> {
            // Create a map to store merged children
            val mergedChildren = mutableMapOf<String, Route>()

            // Add all children from the base directory
            this.children.forEach { child ->
                mergedChildren[child.path] = child
            }

            // Add or merge children from the additional directory
            additional.children.forEach { child ->
                val existing = mergedChildren[child.path]
                if (existing != null) {
                    // If a child with the same path exists, recursively merge them
                    mergedChildren[child.path] = existing.merge(child)
                } else {
                    // Otherwise, add the new child
                    mergedChildren[child.path] = child
                }
            }

            // Return a new directory with the merged children
            Directory(this.path, mergedChildren.values.toMutableList())
        }

        // Both routes are static files
        this is StaticFile && additional is StaticFile -> {
            // Check for conflicts between static files with the same path
            if (this.resourcePath != additional.resourcePath) {
                throw IllegalArgumentException("Conflict: Two StaticFiles with the same path '${this.path}' but different resources.")
            }
            // If they're the same file, return one of them
            this
        }

        // Conflict: One is a Directory and the other is a StaticFile
        else -> {
            throw IllegalArgumentException("Conflict: '${this.path}' exists as both a Directory and a StaticFile.")
        }
    }
}
