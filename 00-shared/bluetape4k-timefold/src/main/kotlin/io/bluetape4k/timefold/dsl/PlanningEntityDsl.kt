package io.bluetape4k.timefold.dsl

import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

data class PlanningEntityDefinition<T: Any>(
    val clazz: KClass<T>,
    val variables: List<KProperty1<T, *>>,
): Serializable

class PlanningEntityBuilder<T: Any>(val clazz: KClass<T>) {
    private val variables = mutableListOf<KProperty1<T, *>>()

    fun planningVariable(prop: KProperty1<T, *>) {
        variables.add(prop)
    }

    fun build(): PlanningEntityDefinition<T> =
        PlanningEntityDefinition(clazz, variables)
}

/**
 * DSL function to create a [PlanningEntityDefinition] for a given class.
 *
 * @param T the type of the planning entity
 * @param block the DSL block to configure the planning entity
 * @return a [PlanningEntityDefinition] for the specified class
 */
inline fun <reified T: Any> planningEntity(
    block: PlanningEntityBuilder<T>.() -> Unit,
): PlanningEntityDefinition<T> =
    PlanningEntityBuilder(T::class).apply(block).build()
