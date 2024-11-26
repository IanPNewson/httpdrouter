package dependencyinjection

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class DIContextElement(val context: DIContext) : AbstractCoroutineContextElement(DIContextElement) {
    companion object Key : CoroutineContext.Key<DIContextElement>
}