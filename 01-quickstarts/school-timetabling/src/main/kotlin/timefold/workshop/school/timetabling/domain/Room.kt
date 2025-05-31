package timefold.workshop.school.timetabling.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import java.io.Serializable

@JsonIdentityInfo(
    scope = Room::class,
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
data class Room(
    @PlanningId
    val id: String = TimebasedUuid.Epoch.nextIdAsString(),
    val name: String = "Room-$id",
): Serializable {

    override fun toString(): String = name
}
