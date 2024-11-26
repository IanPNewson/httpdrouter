package dependencyinjection

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

}


