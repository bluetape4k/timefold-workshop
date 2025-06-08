package timefold.workshop.bed.allocation.domain

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty
import ai.timefold.solver.core.api.domain.solution.PlanningScore
import ai.timefold.solver.core.api.domain.solution.PlanningSolution
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider
import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore
import ai.timefold.solver.core.api.solver.SolverStatus
import com.fasterxml.jackson.annotation.JsonIgnore
import io.bluetape4k.support.hashOf
import java.io.Serializable

@PlanningSolution
data class BedPlan(
    @ProblemFactCollectionProperty
    val departments: MutableList<Department> = mutableListOf(),

    @PlanningEntityCollectionProperty
    val stays: MutableList<Stay> = mutableListOf(),
): Serializable {
    @get:JsonIgnore
    @get:ProblemFactCollectionProperty
    val rooms: MutableList<Room> by lazy { departments.flatMap { it.rooms }.toMutableList() }

    @get:JsonIgnore
    @get:ProblemFactCollectionProperty
    @get:ValueRangeProvider
    val beds: MutableList<Bed> by lazy { rooms.flatMap { it.beds }.toMutableList() }

    @PlanningScore
    var score: HardMediumSoftScore = HardMediumSoftScore.ZERO

    var solverStatus: SolverStatus = SolverStatus.NOT_SOLVING

    constructor(score: HardMediumSoftScore, solverStatus: SolverStatus): this() {
        this.score = score
        this.solverStatus = solverStatus
    }


    override fun equals(other: Any?): Boolean =
        other is BedPlan &&
                departments == other.departments &&
                stays == other.stays

    override fun hashCode(): Int = hashOf(departments, stays)
}
