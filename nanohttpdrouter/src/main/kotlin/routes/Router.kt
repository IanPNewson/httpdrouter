package routes

import TreeNode
import buildZipTree
import routes.authentication.AuthenticationFailedHandler
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipFile
import kotlin.streams.toList

class Router(private val rootRoute : Route, val defaultAuthFailedHandler : AuthenticationFailedHandler? = null) {

    fun findRoute(path: String?): RoutePath? {
        if (path.isNullOrEmpty()) return null

        val pathParts = path.trim('/').split('/')
        return findRouteRecursively(rootRoute, rootRoute.children, pathParts, mutableListOf())
    }

    private fun findRouteRecursively(
        rootRoute: Route,
        routes: List<Route>,
        pathParts: List<String>,
        currentPath: MutableList<RoutePathStep>
    ): RoutePath? {
        if (pathParts.isEmpty()) return null

        val currentPart = pathParts.first()
        val matchedRoute = routes.firstOrNull { it.path == currentPart } ?: return null

        val index = routes.indexOf(matchedRoute)
        currentPath.add(RoutePathStep(matchedRoute, index))

        val remainingParts = pathParts.drop(1)
        if (remainingParts.isEmpty()) {
            return RoutePath(route = matchedRoute, rootRoute = rootRoute, path = currentPath.toList())
        }

        return findRouteRecursively(rootRoute, matchedRoute.children, remainingParts, currentPath)
    }

    companion object {
        fun createRouteTreeFromDirectory(directoryPath: String): Route {
            val rootPath = Paths.get(directoryPath)

            if (!Files.isDirectory(rootPath)) {
                throw IllegalArgumentException("Provided path is not a directory: $directoryPath")
            }

            fun buildRoutesFromPath(currentPath: Path, parentPath: Path = rootPath): Route {
                val relativePath = parentPath.relativize(currentPath).toString().replace("\\", "/")
                val routePath = if (relativePath.isEmpty()) "" else relativePath

                return if (Files.isDirectory(currentPath)) {
                    val children = Files.list(currentPath)
                        .filter { Files.isReadable(it) } // Exclude unreadable files
                        .sorted() // Sort entries alphabetically
                        .map { buildRoutesFromPath(it, currentPath) }
                        .toList().toMutableList()

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

            val zip = ZipFile(zipPath.toFile())
            return createRouteTreeFromZip(zip, topLevelFolder)
        }

        fun createRouteTreeFromZip(zipFile: ZipFile, topLevelFolder: String? = null): Route {
            zipFile.use { zip ->
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
                        Directory(node.name, node.children.map { buildRouteTree(it) }.toMutableList())
                    } else {
                        ZipFileRoute(node.name, zip, node.zipEntry ?: throw IllegalStateException("Missing ZipEntry for file node: ${node.name}"))
                    }
                }

                return buildRouteTree(startingNode)
            }
        }

    }

}

