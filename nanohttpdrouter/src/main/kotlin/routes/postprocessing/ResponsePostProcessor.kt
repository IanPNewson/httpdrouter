package org.iannewson.httpdrouter.routes.postprocessing

import fi.iki.elonen.NanoHTTPD

interface ResponsePostProcessor {
    fun process(response: NanoHTTPD.Response, session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response
}

