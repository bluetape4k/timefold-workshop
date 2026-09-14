package io.bluetape4k.timefold.listshadow

import ai.timefold.solver.core.api.domain.common.PlanningId
import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.variable.IndexShadowVariable
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable
import ai.timefold.solver.core.api.domain.variable.ShadowSources
import ai.timefold.solver.core.api.domain.variable.ShadowVariable
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.SimpleScore
import io.bluetape4k.support.requireNotBlank
import java.util.UUID

/**
 * planning list를 소유하는 최소 route planning entity입니다.
 *
 * [visits]는 solver가 재배치하는 mutable list이므로 외부 caller는 solver가
 * 실행 중인 동안 직접 변경하지 않아야 합니다. 목록의 원소는 별도의
 * [ListShadowVisit] planning entity로 등록됩니다.
 */
@PlanningEntity
data class ListShadowRoute(
    @PlanningId
    val id: String = UUID.randomUUID().toString(),

    @PlanningListVariable(valueRangeProviderRefs = ["visitRange"])
    var visits: MutableList<ListShadowVisit> = mutableListOf(),
) {
    init {
        id.requireNotBlank("id")
    }
}

/**
 * planning list element와 그 element에서 계산되는 선언적 shadow를 보여줍니다.
 */
@PlanningEntity
data class ListShadowVisit(
    @PlanningId
    val id: String = UUID.randomUUID().toString(),
    val label: String = "visit",
) {
    /** route list 안의 0-based 위치를 Timefold이 계산합니다. */
    @IndexShadowVariable(sourceVariableName = "visits")
    var indexInRoute: Int? = null

    /** index에서 파생되는 1-based 위치를 선언적 supplier로 계산합니다. */
    @ShadowVariable(supplierName = "calculateSequencePosition")
    var sequencePosition: Int? = null

    init {
        id.requireNotBlank("id")
        label.requireNotBlank("label")
    }

    /** index shadow가 바뀔 때 sequence position을 순수하게 계산합니다. */
    @ShadowSources("indexInRoute")
    fun calculateSequencePosition(): Int? = indexInRoute?.plus(1)
}

/** route와 list element value range를 함께 제공하는 독립 planning solution입니다. */
@PlanningSolution
data class ListShadowPlan(
    @PlanningEntityCollectionProperty
    val routes: List<ListShadowRoute> = emptyList(),

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "visitRange")
    val visits: List<ListShadowVisit> = emptyList(),

    @PlanningScore
    var score: SimpleScore? = null,
)
