package dependencyinjection

class DIConstructionException(
    val constructingType: Class<*>,
    val context: DIContext,
    val reason: String
) : RuntimeException("Error constructing type ${constructingType.name} via DIContext: $reason")