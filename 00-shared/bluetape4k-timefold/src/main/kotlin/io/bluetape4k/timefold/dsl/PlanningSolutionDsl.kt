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
 * [PlanningSolutionBuilder]를 생성하기 위한 DSL 함수입니다.
 *
 * @param block 플래닝 솔루션을 구성하는 DSL 블록입니다.
 * @return 지정한 설정을 적용한 [PlanningSolutionBuilder] 인스턴스입니다.
 */
inline fun planningSolution(block: PlanningSolutionBuilder.() -> Unit): PlanningSolutionBuilder {
    return PlanningSolutionBuilder().apply(block)
}
