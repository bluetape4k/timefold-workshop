package timefold.workshop.school.timetabling.domain

import ai.timefold.solver.core.api.domain.common.PlanningId
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.bluetape4k.idgenerators.uuid.Uuid
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalTime

@JsonIdentityInfo(
    scope = Timeslot::class,
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
data class Timeslot(
    @PlanningId
    val id: String = Uuid.V7.nextIdAsString(),
    var dayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    var startTime: LocalTime = LocalTime.of(8, 0),
    var endTime: LocalTime = startTime.plusMinutes(50),
): Serializable {

    override fun toString(): String = "$dayOfWeek $startTime"
}
