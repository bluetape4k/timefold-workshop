package timefold.workshop.school.timetabling.domain

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.solver.SolverStatus
import io.bluetape4k.ToStringBuilder
import java.io.Serializable

@PlanningSolution
data class Timetable(
    val name: String = "Default Timetable",

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    val timeslots: List<Timeslot> = emptyList(),

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    val rooms: List<Room> = emptyList(),

    @PlanningEntityCollectionProperty
    val lessons: List<Lesson> = emptyList(),

    @PlanningScore
    var score: HardSoftScore? = null,

    var solverStatus: SolverStatus? = null,
): Serializable {

    constructor(name: String, score: HardSoftScore?, solverStatus: SolverStatus)
            : this(name, emptyList(), emptyList(), emptyList(), score, solverStatus)

    override fun toString(): String = ToStringBuilder(this)
        .add("name", name)
        .add("score", score)
        .add("solverStatus", solverStatus)
        .toString()
}
