package timefold.workshop.school.timetabling.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.lookup.PlanningId
import ai.timefold.solver.core.api.domain.variable.PlanningVariable
import com.fasterxml.jackson.annotation.JsonIdentityReference
import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import io.bluetape4k.support.requireNotBlank
import java.io.Serializable

@PlanningEntity
data class Lesson(
    @PlanningId
    val id: String = TimebasedUuid.Epoch.nextIdAsString(),
    val subject: String = "subject-$id",
    val teacher: String = "teacher-$id",
    val studentGroup: String = "studentGroup-$id",
): Serializable {

    @JsonIdentityReference
    @PlanningVariable
    var timeslot: Timeslot? = null

    @JsonIdentityReference
    @PlanningVariable
    var room: Room? = null

    constructor(
        id: String,
        subject: String,
        teacher: String,
        studentGroup: String,
        timeslot: Timeslot?,
        room: Room?,
    ): this(id, subject, teacher, studentGroup) {
        id.requireNotBlank("id")

        this.timeslot = timeslot
        this.room = room
    }

    override fun toString(): String = "$subject($id)"
}
