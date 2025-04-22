package io.bluetape4k.timefold.dsl

import kotlin.reflect.KClass

class SolverFactoryDsl<T: Any>(
    val planningSolutionClass: KClass<T>,
    val constraints: List<ConstraintDefinition>,
) {
    fun build(): String {
        println("🧩 SolverFactory created for: ${planningSolutionClass.simpleName}")
        constraints.forEach { println("→ constraint: ${it.name} (${if (it.isHard) "HARD" else "SOFT"})") }
        return "MockSolverFactoryInstance"
    }
}

inline fun <reified T: Any> solverFactory(
    constraints: List<ConstraintDefinition>,
): SolverFactoryDsl<T> =
    SolverFactoryDsl(T::class, constraints)
