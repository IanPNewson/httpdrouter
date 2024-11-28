package org.iannewson.httpdrouter.routes

import org.iannewson.httpdrouter.MimeTypes
import org.iannewson.httpdrouter.dependencyinjection.DIContext
import org.iannewson.httpdrouter.extension
import org.iannewson.httpdrouter.routes.authentication.Authenticator
import org.iannewson.httpdrouter.routes.postprocessing.ResponsePostProcessor

/**
 * Represents a base class for defining HTTP routes in a hierarchical structure.
 *
 * A `Route` corresponds to a specific path and can optionally contain child routes, an authenticator,
 * and post-processing logic for responses. Subclasses of this class should implement the actual
 * behavior for handling requests and generating responses.
 *
 * @property path The relative path of the route. It must not contain `/` unless it's the first character.
 * @property children A mutable list of child routes nested under this route.
 * @property authenticationHandler An optional handler for authenticating requests to this route.
 * @property postProcessors A list of post-processors to apply to responses generated by this route.
 * @throws RuntimeException If the `path` contains `/` in an invalid position.
 */
abstract class Route(
    val path: String,
    val children: MutableList<Route> = mutableListOf(),
    val authenticationHandler: Authenticator? = null,
    private val postProcessors: MutableList<ResponsePostProcessor> = mutableListOf()
) {

    init {
        if (path.indexOf("/") > -1) {
            throw RuntimeException("Route paths should all be relative and therefore shouldn't contain / (unless it's the first character)")
        }
    }

    /**
     * Retrieves the route handler responsible for processing requests to this route.
     *
     * Subclasses must implement this method to return the appropriate [RouteHandler].
     *
     * @param diContext The dependency injection context used to resolve route dependencies.
     * @return The handler for processing requests to this route.
     */
    abstract fun getRouteHandler(diContext: DIContext): RouteHandler

    /**
     * The file extension of the route's path, if applicable.
     */
    open val extension: String?
        get() = this.path.extension()

    /**
     * The MIME type associated with the route's file extension, if applicable.
     */
    val mimeType: String?
        get() {
            val ext = extension ?: return null
            return MimeTypes[ext]
        }

    /**
     * Adds one or more child routes to this route.
     *
     * @param child The child routes to add.
     * @return The current route instance for chaining.
     */
    open fun addChildren(vararg child: Route): Route {
        this.children.addAll(child)
        return this
    }

    /**
     * Adds a response post-processor to this route.
     *
     * Post-processors modify the response before it is sent to the client.
     *
     * @param postProcessor The post-processor to add.
     * @return The current route instance for chaining.
     */
    fun addPostProcessor(postProcessor: ResponsePostProcessor): Route {
        this.postProcessors.add(postProcessor)
        return this
    }

    /**
     * Collects all post-processors defined for this route.
     *
     * Subclasses can override this to add additional post-processors or customize the collection logic.
     *
     * @return A list of post-processors for this route.
     */
    open fun collectPostProcessors(): List<ResponsePostProcessor> = postProcessors

    /**
     * Provides a string representation of the route tree starting from this route.
     *
     * @return A string representation of the route tree.
     */
    override fun toString(): String {
        return describeRouteTree(this)
    }

    companion object {

        /**
         * Describes the tree structure of the given route and its descendants.
         *
         * @param route The root route to describe.
         * @return A string representation of the route tree.
         */
        fun describeRouteTree(route: Route): String {
            val str = StringBuilder()
            describeRouteTree(route, "", str)
            return str.toString()
        }

        /**
         * Helper method to recursively build a string representation of a route tree.
         *
         * @param route The current route to describe.
         * @param indent The current indentation level for pretty-printing.
         * @param builder The string builder to append the description to.
         */
        fun describeRouteTree(route: Route, indent: String, builder: StringBuilder = StringBuilder()) {
            if (route is DefaultDocument) {
                builder.appendLine("$indent${route::class.simpleName}: ${route.target.path}")
            } else {
                builder.appendLine("$indent${route::class.simpleName}: ${route.path}")
            }

            route.children.forEach { child ->
                describeRouteTree(child, "$indent  ", builder)
            }
        }
    }
}
