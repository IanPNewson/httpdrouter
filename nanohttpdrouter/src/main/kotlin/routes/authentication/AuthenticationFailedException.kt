package routes.authentication

import routes.Route

class AuthenticationFailedException(val route : Route,
                                    val authHandler : Authenticator
) : Exception()