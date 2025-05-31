package timefold.workshop.school.timetabling.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.bluetape4k.idgenerators.uuid.TimebasedUuid
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
    val id: String = TimebasedUuid.Epoch.nextIdAsString(),
    var dayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    var startTime: LocalTime = LocalTime.of(8, 0),
    var endTime: LocalTime = startTime.plusMinutes(50),
): Serializable {

    override fun toString(): String = "$dayOfWeek $startTime"
}
