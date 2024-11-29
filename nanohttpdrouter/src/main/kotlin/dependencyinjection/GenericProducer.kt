package org.iannewson.httpdrouter.dependencyinjection

import kotlin.reflect.KClass

internal class GenericProducer(val clazz: Class<*>) : Producer(0) {

    init {

    }

    override fun produce(context: DIContext): Any {
        if (clazz == Unit.javaClass)
            throw DIConstructionException(clazz, context, "Cannot construct an instance of Unit, and if you think about it you wouldn't want me to")

        val ctors = clazz.constructors.sortedByDescending { it.parameterCount }
        if (ctors.isEmpty()) {
            //check stack
            throw DIConstructionException(clazz, context, "No constructor")
        }
        val ctor = ctors.first()
        val args = ctor.parameters.map {
            context.get(it.type)
        }.toTypedArray()
        return ctor.newInstance(*args)
    }

    override val returnType: KClass<*>
        get() = clazz.kotlin
}