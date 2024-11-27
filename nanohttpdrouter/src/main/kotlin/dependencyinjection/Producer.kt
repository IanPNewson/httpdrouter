package org.iannewson.httpdrouter.dependencyinjection

import kotlin.reflect.KClass

abstract class Producer(val numArgs: Int) {

    abstract fun produce(context: DIContext): Any

    abstract val returnType: KClass<*>

}