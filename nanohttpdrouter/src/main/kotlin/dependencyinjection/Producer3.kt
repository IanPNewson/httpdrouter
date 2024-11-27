package org.iannewson.httpdrouter.dependencyinjection

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class Producer3<T : Any, U : Any, V : Any>(
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