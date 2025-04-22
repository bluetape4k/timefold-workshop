package io.bluetape4k.timefold.dsl

class PlanningSolutionBuilder {
    val entities = mutableListOf<Any>()
    var score: Any? = null

    fun add(entity: Any) = entities.add(entity)
    fun score(score: Any) {
        this.score = score
    }
}

inline fun planningSolution(block: PlanningSolutionBuilder.() -> Unit): PlanningSolutionBuilder {
    return PlanningSolutionBuilder().apply(block)
}
