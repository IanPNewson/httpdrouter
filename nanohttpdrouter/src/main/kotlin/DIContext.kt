import kotlin.reflect.KClass

class DIContext {
    companion object {
        val singleton: DIContext by lazy {
            DIContext()
        }
    }

    private val producers = mutableListOf<Producer>()

    private fun producers() :List<Producer> = producers

    //region one shot

    inline fun <reified T, reified U> get(producer :(U) -> T) : T {
        val arg = this.get<U>()
        val instance = producer(arg)
        return instance
    }

    //endregion

    inline fun <reified T> get() : T {
        val clazz = T::class.java
        return get(clazz) as T
    }

    fun get(clazz:Class<*>): Any {
        val producers1 = producers()

        val matches = producers1
            .filter { clazz == it.returnType.java }
            .sortedByDescending { it.numArgs }

        if (!matches.any())
            throw RuntimeException("No producer for type $clazz")

        val producer = matches.first()

        if (cache.containsKey(producer)) {
            return cache[producer] as Any
        }

        val result = producer.produce(this)

        cache[producer] = result

        return result
    }

    private val cache = mutableMapOf<Producer,Any>()

    inline fun <reified T : Any> add(noinline producer :() -> T) {
        add(T::class, producer)
    }

    fun <T : Any> add(returnClass : KClass<T>, producer :() -> T) {
        producers.add(Producer1(producer, returnClass))
    }

    inline fun <reified T : Any, reified U : Any> add(noinline producer :(U) -> T) {
        add(T::class, U::class, producer)
    }

    fun <T : Any, U:Any> add(returnClass : KClass<T>, argClass  : KClass<U>, producer :(U) -> T) {
        producers.add(Producer2(producer, returnClass, argClass))
    }

    inline fun <reified T : Any, reified U : Any, reified V : Any> add(noinline producer :(U, V) -> T) {
        add(T::class, U::class, V::class, producer)
    }

    fun <T : Any, U:Any, V:Any> add(
        returnClass : KClass<T>,
        arg1Class  : KClass<U>,
        arg2Class  : KClass<V>,
        producer :(U,V) -> T) {
        producers.add(Producer3(producer, returnClass, arg1Class, arg2Class))
    }

    abstract class Producer(val numArgs :Int) {

        abstract fun produce(context: DIContext) :Any

        abstract val returnType : KClass<*>

    }

    class Producer1<T : Any>(
        val producer :() -> T,
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
        val producer :(U) -> T,
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
        val producer :(U,V) -> T,
        override val returnType: KClass<*>,
                            private val arg1Clazz: KClass<U>,
                            private val arg2Clazz: KClass<V>) : Producer(2) {

        override fun produce(context: DIContext): Any {
            return producer(
                context.get(arg1Clazz.java) as U,
                context.get(arg2Clazz.java) as V
            )
        }

    }

}