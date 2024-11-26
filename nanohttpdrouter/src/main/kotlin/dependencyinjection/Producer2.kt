package dependencyinjection

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
internal class Producer2<T : Any, U : Any>(
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