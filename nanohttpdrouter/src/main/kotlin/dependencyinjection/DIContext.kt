package dependencyinjection

import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.reflect.KClass

/**
 * A lightweight dependency injection framework that supports registering producers
 * for different types of objects and resolves dependencies on demand.
 *
 * This framework allows caching of produced instances, supports producers
 * with zero, one, or two arguments, and resolves dependencies recursively.
 */
class DIContext {

    // A list of registered producers for creating dependencies
    private val producers = mutableListOf<Producer>()

    // Cache to store already-created dependencies for reuse
    private val cache = mutableMapOf<Producer, Any>()

    // Used track the construction stack for error reporting
    private val resolvingStack: ThreadLocal<ArrayDeque<Class<*>>> = ThreadLocal.withInitial { ArrayDeque() }


    /**
     * Registers a zero-argument producer for a specific type.
     *
     * @param T The type of the object produced.
     * @param producer The function to produce the object.
     */
    inline fun <reified T : Any> add(noinline producer: () -> T) {
        add(T::class, producer)
    }

    /**
     * Registers a zero-argument producer for a specific type.
     *
     * @param T The type of the object produced.
     * @param returnClass The class of the object produced.
     * @param producer The function to produce the object.
     */
    fun <T : Any> add(returnClass: KClass<T>, producer: () -> T) {
        producers.add(Producer1(producer, returnClass))
    }

    /**
     * Registers a one-argument producer for a specific type.
     *
     * @param T The type of the object produced.
     * @param U The type of the argument the producer takes.
     * @param producer The function to produce the object, taking one argument.
     */
    inline fun <reified T : Any, reified U : Any> add(noinline producer: (U) -> T) {
        add(T::class, U::class, producer)
    }

    /**
     * Registers a one-argument producer for a specific type.
     *
     * @param T The type of the object produced.
     * @param U The type of the argument the producer takes.
     * @param returnClass The class of the object produced.
     * @param argClass The class of the argument the producer takes.
     * @param producer The function to produce the object, taking one argument.
     */
    fun <T : Any, U : Any> add(returnClass: KClass<T>, argClass: KClass<U>, producer: (U) -> T) {
        producers.add(Producer2(producer, returnClass, argClass))
    }

    /**
     * Registers a two-argument producer for a specific type.
     *
     * @param T The type of the object produced.
     * @param U The type of the first argument the producer takes.
     * @param V The type of the second argument the producer takes.
     * @param producer The function to produce the object, taking two arguments.
     */
    inline fun <reified T : Any, reified U : Any, reified V : Any> add(noinline producer: (U, V) -> T) {
        add(T::class, U::class, V::class, producer)
    }

    /**
     * Registers a two-argument producer for a specific type.
     *
     * @param T The type of the object produced.
     * @param U The type of the first argument the producer takes.
     * @param V The type of the second argument the producer takes.
     * @param returnClass The class of the object produced.
     * @param arg1Class The class of the first argument.
     * @param arg2Class The class of the second argument.
     * @param producer The function to produce the object, taking two arguments.
     */
    fun <T : Any, U : Any, V : Any> add(
        returnClass: KClass<T>,
        arg1Class: KClass<U>,
        arg2Class: KClass<V>,
        producer: (U, V) -> T
    ) {
        producers.add(Producer3(producer, returnClass, arg1Class, arg2Class))
    }

    /**
     * Resolves and retrieves an instance of the specified type.
     *
     * If no producer is registered for the type, attempts to create a generic instance.
     * If the instance is already cached, it is returned directly.
     *
     * @param clazz The class of the object to retrieve.
     * @return The resolved object.
     */
    fun get(clazz: Class<*>): Any {
        val stack = resolvingStack.get()

        // Detect circular dependencies
        if (stack.contains(clazz)) {
            val cycle = stack.joinToString(" -> ") { it.name }
            throw IllegalStateException("Circular dependency detected: $cycle -> ${clazz.name}")
        }

        stack.addFirst(clazz) // Push the current class to the stack

        try {
            val matches = producers
                .filter { clazz == it.returnType.java }
                .sortedByDescending { it.numArgs }

            val producer: Producer = if (matches.isEmpty()) {
                GenericProducer(clazz)
            } else {
                matches.first()
            }

            // Return cached instance if available
            if (cache.containsKey(producer)) {
                return cache[producer] as Any
            }

            // Produce and cache the instance
            val result = producer.produce(this)
            cache[producer] = result
            return result
        } catch (e: Exception) {
            // Include stack trace in error message
            val dependencyPath = stack.joinToString(" -> ") { it.name }
            throw IllegalStateException(
                "Error resolving dependency for ${clazz.name}. Dependency stack: $dependencyPath",
                e
            )
        } finally {
            stack.removeFirst() // Ensure the class is removed from the stack even if an exception occurs
        }
    }


    /**
     * Resolves and retrieves an instance of the specified type using type inference.
     *
     * @param T The type of the object to retrieve.
     * @return The resolved object.
     */
    inline fun <reified T> get(): T {
        val clazz = T::class.java
        return get(clazz) as T
    }

    /**
     * Resolves and retrieves an instance of the specified type by invoking the provided producer.
     *
     * @param T The type of the object to retrieve.
     * @param producer The function to produce the object.
     * @return The resolved object.
     */
    inline fun <reified T> get(producer: () -> T): T {
        return producer()
    }

    /**
     * Resolves and retrieves an instance of the specified type with one dependency.
     *
     * @param T The type of the object to retrieve.
     * @param U The type of the dependency.
     * @param producer The function to produce the object, taking one argument.
     * @return The resolved object.
     */
    inline fun <reified T, reified U> get(producer: (U) -> T): T {
        val arg = this.get<U>()
        return producer(arg)
    }

    /**
     * Resolves and retrieves an instance of the specified type with two dependencies.
     *
     * @param T The type of the object to retrieve.
     * @param U The type of the first dependency.
     * @param V The type of the second dependency.
     * @param producer The function to produce the object, taking two arguments.
     * @return The resolved object.
     */
    inline fun <reified T, reified U, reified V> get(producer: (U, V) -> T): T {
        val arg1 = this.get<U>()
        val arg2 = this.get<V>()
        return producer(arg1, arg2)
    }
}
