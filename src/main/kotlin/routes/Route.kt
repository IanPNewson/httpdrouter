package routes

import DefaultDocument
import MimeTypes
import extension
import fi.iki.elonen.NanoHTTPD

// Base Route class and its subclasses for different route types
abstract class Route(val path: String, val children: List<Route> = mutableListOf()) {

    init {
        if (path.lastIndexOf("/") > 0) {
            throw RuntimeException("Route paths should all be relative and therefore shouldn't contain / (unless it's the first character)")
        }
    }

    abstract fun response(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response

    open val extension: String?
        get() = this.path.extension()

    val mimeType: String?
        get() {
            val ext = extension ?: return null
            return MimeTypes[ext]
        }

    open fun addChildren(vararg child: Route): Route {
        (this.children as MutableList<Route>).addAll(child)
        return this
    }

    override fun toString(): String {
        return Companion.describeRouteTree(this)
    }

    companion object {

        fun describeRouteTree(route: Route): String {
            val str = java.lang.StringBuilder()
            describeRouteTree(route, "", str)
            return str.toString()
        }

        fun describeRouteTree(route: Route, indent: String, builder: StringBuilder = java.lang.StringBuilder()) {

            if (route is DefaultDocument) {
                builder.appendLine("$indent${route::class.simpleName}: ${route.route.path}")
            } else {
                builder.appendLine("$indent${route::class.simpleName}: ${route.path}")
            }

            route.children.forEach { child ->
                describeRouteTree(child, "$indent  ", builder)
            }
        }

    }

    fun Route.merge(base: Route, additional: Route): Route {
        if (base.path != additional.path) {
            throw IllegalArgumentException("Cannot merge routes with different root paths: '${base.path}' and '${additional.path}'")
        }

        return when {
            base is Directory && additional is Directory -> {
                // Merge directories
                val mergedChildren = mutableMapOf<String, Route>()

                // Add all children from the base directory
                base.children.forEach { child ->
                    mergedChildren[child.path] = child
                }

                // Add or merge children from the additional directory
                additional.children.forEach { child ->
                    val existing = mergedChildren[child.path]
                    if (existing != null) {
                        // Recursively merge if a child with the same path exists
                        mergedChildren[child.path] = merge(existing, child)
                    } else {
                        // Add the new child
                        mergedChildren[child.path] = child
                    }
                }

                // Return a new merged Directory
                Directory(base.path, mergedChildren.values.toList())
            }

            base is StaticFile && additional is StaticFile -> {
                // Conflict: Two StaticFiles with the same path
                if (base.resourcePath != additional.resourcePath) {
                    throw IllegalArgumentException("Conflict: Two StaticFiles with the same path '${base.path}' but different resources.")
                }
                // If they're the same file, return one of them
                base
            }

            else -> {
                // Conflict: One is a Directory and the other is a StaticFile
                throw IllegalArgumentException("Conflict: '${base.path}' exists as both a Directory and a StaticFile.")
            }
        }
    }
}


