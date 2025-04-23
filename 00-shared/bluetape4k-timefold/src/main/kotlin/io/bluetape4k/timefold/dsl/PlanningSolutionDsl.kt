package io.bluetape4k.timefold.dsl

/*
 * DSL for defining a planning solution.
 *
 * @property entities the list of entities in the planning solution
 * @property score the score of the planning solution
 */
class PlanningSolutionBuilder {
    val entities = mutableListOf<Any>()
    var score: Any? = null

    fun add(entity: Any) = entities.add(entity)
    fun score(score: Any) {
        this.score = score
    }
}

/**
 * DSL function to create a [PlanningSolutionBuilder].
 *
 * @param block the DSL block to configure the planning solution
 * @return a [PlanningSolutionBuilder] for the specified class
 */
inline fun planningSolution(block: PlanningSolutionBuilder.() -> Unit): PlanningSolutionBuilder {
    return PlanningSolutionBuilder().apply(block)
}
