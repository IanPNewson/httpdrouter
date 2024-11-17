import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class Router(val rootRoute :Route) {

    fun findRoute(path: String?): Route? {
        if (path == null) return null
        val pathParts = path.trim('/').split('/')
        return findRouteRecursively(rootRoute.children, pathParts)
    }

    private fun findRouteRecursively(routes: List<Route>, pathParts: List<String>): Route? {
        if (pathParts.isEmpty()) return null
        val currentPart = pathParts.first()
        val matchedRoute = routes.firstOrNull { it.path == currentPart } ?: return null
        val remainingParts = pathParts.drop(1)
        val childRoute = findRouteRecursively(matchedRoute.children, remainingParts)
        return childRoute ?: matchedRoute
    }

    companion object {
        fun createRouteTreeFromDirectory(directoryPath: String, defaultDocument: String? = "index.html"): Route {
            val rootPath = Paths.get(directoryPath)

            if (!Files.isDirectory(rootPath)) {
                throw IllegalArgumentException("Provided path is not a directory: $directoryPath")
            }

            fun buildRoutesFromPath(currentPath: Path, parentPath: Path = rootPath): Route {
                val relativePath = parentPath.relativize(currentPath).toString().replace("\\", "/")
                val routePath = if (relativePath.isEmpty()) "/" else relativePath

                return if (Files.isDirectory(currentPath)) {
                    val children = Files.list(currentPath)
                        .filter { Files.isReadable(it) } // Exclude unreadable files
                        .sorted() // Sort entries alphabetically
                        .map { buildRoutesFromPath(it, currentPath) } // Recursively process children
                        .toList()

                    val directoryRoute = Directory(routePath, children)

                    // If a default document is specified and exists, add it to the directory
                    if (defaultDocument != null) {
                        val defaultFilePath = currentPath.resolve(defaultDocument)
                        if (Files.exists(defaultFilePath)) {
                            val defaultFileRoute = StaticFile(routePath, defaultFilePath.toAbsolutePath().toString())
                            return Directory(routePath, children + defaultFileRoute)
                        }
                    }

                    directoryRoute
                } else {
                    StaticFile(routePath, currentPath.toAbsolutePath().toString())
                }
            }

            return buildRoutesFromPath(rootPath)
        }

    }

}