package ru.Blays.ReVanced.Manager.DI

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.java.KoinJavaComponent

public inline fun <reified T> autoInject(
    clazz: Class<*> = T::class.java,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        KoinJavaComponent.get(clazz, qualifier, parameters)
    }
}