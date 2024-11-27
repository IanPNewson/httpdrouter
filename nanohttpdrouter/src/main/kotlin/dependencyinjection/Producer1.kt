package org.iannewson.httpdrouter.dependencyinjection

import kotlin.reflect.KClass

internal class Producer1<T : Any>(
    val producer: () -> T,
    private val clazz: KClass<T>
) : Producer(0) {

    override fun produce(context: DIContext): Any {
        return producer()
    }

    override val returnType: KClass<*>
        get() = this.clazz

}