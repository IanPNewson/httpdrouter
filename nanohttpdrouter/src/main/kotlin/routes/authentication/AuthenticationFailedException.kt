package org.iannewson.httpdrouter.routes.authentication

import org.iannewson.httpdrouter.routes.Route

class AuthenticationFailedException(val route : Route,
                                    val authHandler : Authenticator
) : Exception()