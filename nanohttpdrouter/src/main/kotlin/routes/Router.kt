package routes

import TreeNode
import buildZipTree
import routes.authentication.AuthenticationFailedHandler
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile

class Router(private val rootRoute : Route, val defaultAuthFailedHandler : AuthenticationFailedHandler? = null) {

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

    fun findRoutePath(target: Route): RoutePath {
        fun traverse(current: Route, currentPath: MutableList<RoutePathStep>): RoutePath? {
            if (current == target) {
                currentPath.add(RoutePathStep(current, currentPath.last().route.children.indexOf(current)))
                return RoutePath(target, rootRoute, currentPath.toList())
            }

            // Recursively search in children if current is a Directory
            if (current is Directory) {
                current.children.forEachIndexed { index, child ->
                    currentPath.add(RoutePathStep(current, index))
                    val result = traverse(child, currentPath)
                    if (result != null) return result
                    currentPath.removeAt(currentPath.size - 1)
                }
            }
            return null
        }

        val path = traverse(rootRoute, mutableListOf())

        return path ?:
            throw RuntimeException("Couldn't find path for route '${target::class.simpleName} ${target.path}'")
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
                    var children = Files.list(currentPath)
                        .filter { Files.isReadable(it) } // Exclude unreadable files
                        .sorted() // Sort entries alphabetically
                        .map { buildRoutesFromPath(it, currentPath) } // Recursively process children
                        .toList()

                    // If a default document is specified and exists, add it to the directory
                    if (defaultDocument != null) {
                        val defaultFilePath = currentPath.resolve(defaultDocument)
                        if (Files.exists(defaultFilePath)) {
                            val defaultFileRoute = StaticFile("", defaultFilePath.toAbsolutePath().toString())
                            children = children + defaultFileRoute
                        }
                    }

                    val directoryRoute = Directory(routePath, children)

                    directoryRoute
                } else {
                    StaticFile(routePath, currentPath.toAbsolutePath().toString())
                }
            }

            return buildRoutesFromPath(rootPath)
        }
        fun createRouteTreeFromZip(zipFilePath: String, topLevelFolder: String? = null): Route {
            val zipPath = Path.of(zipFilePath)

            if (!zipPath.toFile().exists() || !zipPath.toFile().isFile) {
                throw IllegalArgumentException("Provided path is not a valid ZIP file: $zipFilePath")
            }

            ZipFile(zipPath.toFile()).use { zip ->
                // Build the TreeNode structure
                val rootTreeNode = zip.buildZipTree()

                // Find the starting node based on the top-level folder
                val startingNode = if (!topLevelFolder.isNullOrEmpty()) {
                    rootTreeNode.children.find { it.name == topLevelFolder && it.isDirectory }
                        ?: throw IllegalArgumentException("Top-level folder '$topLevelFolder' not found in ZIP file.")
                } else {
                    rootTreeNode
                }

                // Recursively build the Route tree from the TreeNode structure
                fun buildRouteTree(node: TreeNode): Route {
                    return if (node.isDirectory) {
                        Directory(node.name, node.children.map { buildRouteTree(it) })
                    } else {
                        ZipFileRoute(node.name, zipPath, node.zipEntry ?: throw IllegalStateException("Missing ZipEntry for file node: ${node.name}"))
                    }
                }

                return buildRouteTree(startingNode)
            }
        }

    }

}

