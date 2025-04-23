package io.bluetape4k.timefold.dsl

import kotlin.reflect.KClass

/**
 * DSL for defining a solver factory.
 *
 * @param T the type of the planning solution
 * @property planningSolutionClass the class of the planning solution
 * @property constraints the list of constraint definitions
 */
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

/**
 * DSL function to create a [SolverFactoryDsl] for a given class.
 *
 * @param T the type of the planning solution
 * @param constraints the list of constraint definitions
 * @return a [SolverFactoryDsl] for the specified class
 */
inline fun <reified T: Any> solverFactory(
    constraints: List<ConstraintDefinition>,
): SolverFactoryDsl<T> =
    SolverFactoryDsl(T::class, constraints)
