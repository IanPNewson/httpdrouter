package dependencyinjection

import kotlin.reflect.KClass

internal class GenericProducer(val clazz: Class<*>) : Producer(0) {
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