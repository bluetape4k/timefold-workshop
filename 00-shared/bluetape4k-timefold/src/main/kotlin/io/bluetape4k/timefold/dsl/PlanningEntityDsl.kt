package io.bluetape4k.timefold.dsl

import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class PlanningEntityBuilder<T: Any>(val clazz: KClass<T>) {
    private val variables = mutableListOf<KProperty1<T, *>>()

    fun planningVariable(prop: KProperty1<T, *>) {
        variables.add(prop)
    }

    fun build(): PlanningEntityDefinition<T> =
        PlanningEntityDefinition(clazz, variables)

}


data class PlanningEntityDefinition<T: Any>(
    val clazz: KClass<T>,
    val variables: List<KProperty1<T, *>>,
): Serializable

inline fun <reified T: Any> planningEntity(
    block: PlanningEntityBuilder<T>.() -> Unit,
): PlanningEntityDefinition<T> {
    val builder = PlanningEntityBuilder(T::class)
    builder.block()
    return builder.build()
}
