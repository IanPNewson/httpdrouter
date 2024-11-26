import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

class DIContext {
    companion object {
//        val singleton: DIContext by lazy {
//            DIContext()
//        }

    }

    private val producers = mutableListOf<Producer>()

    private fun producers(): List<Producer> = producers

    //region one shot

    inline fun <reified T> get(producer: () -> T): T {
        val instance = producer()
        return instance
    }

    inline fun <reified T, reified U> get(producer: (U) -> T): T {
        val arg = this.get<U>()
        val instance = producer(arg)
        return instance
    }

    inline fun <reified T, reified U, reified V> get(producer: (U, V) -> T): T {
        val arg1 = this.get<U>()
        val arg2 = this.get<V>()
        val instance = producer(arg1, arg2)
        return instance
    }

    //endregion

    inline fun <reified T> get(): T {
        val clazz = T::class.java
        return get(clazz) as T
    }

    fun get(clazz: Class<*>): Any {
        val producers1 = producers()

        val matches = producers1
            .filter { clazz == it.returnType.java }
            .sortedByDescending { it.numArgs }

        val producer: Producer

        if (!matches.any()) {
//            throw RuntimeException("No producer for type $clazz")
            producer = GenericProducer(clazz)
        } else {
            producer = matches.first()
        }

        if (cache.containsKey(producer)) {
            return cache[producer] as Any
        }

        val result = producer.produce(this)

        cache[producer] = result

        return result
    }

    private val cache = mutableMapOf<Producer, Any>()

    inline fun <reified T : Any> add(noinline producer: () -> T) {
        add(T::class, producer)
    }

    fun <T : Any> add(returnClass: KClass<T>, producer: () -> T) {
        producers.add(Producer1(producer, returnClass))
    }

    inline fun <reified T : Any, reified U : Any> add(noinline producer: (U) -> T) {
        add(T::class, U::class, producer)
    }

    fun <T : Any, U : Any> add(returnClass: KClass<T>, argClass: KClass<U>, producer: (U) -> T) {
        producers.add(Producer2(producer, returnClass, argClass))
    }

    inline fun <reified T : Any, reified U : Any, reified V : Any> add(noinline producer: (U, V) -> T) {
        add(T::class, U::class, V::class, producer)
    }

    fun <T : Any, U : Any, V : Any> add(
        returnClass: KClass<T>,
        arg1Class: KClass<U>,
        arg2Class: KClass<V>,
        producer: (U, V) -> T
    ) {
        producers.add(Producer3(producer, returnClass, arg1Class, arg2Class))
    }

    abstract class Producer(val numArgs: Int) {

        abstract fun produce(context: DIContext): Any

        abstract val returnType: KClass<*>

    }

    class Producer1<T : Any>(
        val producer: () -> T,
        private val clazz: KClass<T>
    ) : Producer(0) {

        override fun produce(context: DIContext): Any {
            return producer()
        }

        override val returnType: KClass<*>
            get() = this.clazz

    }

    @Suppress("UNCHECKED_CAST")
    class Producer2<T : Any, U : Any>(
        val producer: (U) -> T,
        private val returnClazz: KClass<T>,
        private val argClazz: KClass<U>
    ) : Producer(1) {

        @Suppress("UNCHECKED_CAST")
        override fun produce(context: DIContext): Any {
            return producer(context.get(argClazz.java) as U)
        }

        override val returnType: KClass<*>
            get() = this.returnClazz

    }

    @Suppress("UNCHECKED_CAST")
    private class Producer3<T : Any, U : Any, V : Any>(
        val producer: (U, V) -> T,
        override val returnType: KClass<*>,
        private val arg1Clazz: KClass<U>,
        private val arg2Clazz: KClass<V>
    ) : Producer(2) {

        override fun produce(context: DIContext): Any {
            return producer(
                context.get(arg1Clazz.java) as U,
                context.get(arg2Clazz.java) as V
            )
        }

    }

    private class GenericProducer(val clazz: Class<*>) : Producer(0) {
        override fun produce(context: DIContext): Any {
            val ctors = clazz.constructors.sortedByDescending { it.parameterCount }
            if (ctors.isEmpty()) throw DIConstructionException(clazz, context, "No constructor")
            val ctor = ctors.first()
            val args = ctor.parameters.map {
                context.get(it.type)
            }.toTypedArray()
            return ctor.newInstance(*args)
        }

        override val returnType: KClass<*>
            get() = clazz.kotlin
    }

}

class DIContextElement(val context: DIContext) : AbstractCoroutineContextElement(DIContextElement) {
    companion object Key : CoroutineContext.Key<DIContextElement>
}

val CoroutineContext.diContext: DIContext
    get() = (this[DIContextElement]?.context
        ?: throw IllegalStateException("DIContext not found in CoroutineContext"))

fun CoroutineScope.withDIContext(context: DIContext, block: suspend CoroutineScope.() -> Unit) {
    // Create a new scope by adding the DIContextElement to the existing coroutine context
    val newScope = CoroutineScope(this.coroutineContext + DIContextElement(context))

    // Launch the block in the new scope
    newScope.launch {
        block()
    }
}

class DIConstructionException(
    val constructingType: Class<*>,
    val context: DIContext,
    val reason: String
) : RuntimeException("Error constructing type ${constructingType.name} via DIContext: $reason")
