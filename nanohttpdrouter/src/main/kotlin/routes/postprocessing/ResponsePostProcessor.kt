package org.iannewson.httpdrouter.routes.postprocessing

import fi.iki.elonen.NanoHTTPD

/**
 * Defines an interface for processing HTTP responses before they are sent to the client.
 *
 * Classes implementing this interface can modify or enhance a `NanoHTTPD.Response` object
 * based on the context of the HTTP session.
 */
interface ResponsePostProcessor {

    /**
     * Processes and potentially modifies the provided HTTP response.
     *
     * This method is called after the initial response is generated but before it is sent
     * back to the client. It allows for operations such as adding headers, logging, or
     * transforming the response content.
     *
     * @param response The original response to be processed.
     * @param session The HTTP session associated with the request, providing context such as
     *                headers, query parameters, and other session data.
     * @return The processed response to be sent to the client.
     */
    fun process(response: NanoHTTPD.Response, session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response
}
