package io.github.techtastic.vc.registry

interface RegistrySupplier<T> {

    val name: String
    fun get(): T

}