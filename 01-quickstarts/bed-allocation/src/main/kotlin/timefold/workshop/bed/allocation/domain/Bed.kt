package timefold.workshop.bed.allocation.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.bluetape4k.idgenerators.uuid.TimebasedUuid

/**
 * 병원의 특정 병실([Room])의 병상([Bed])을 나타내는 클래스입니다.
 */
@JsonIdentityInfo(
    scope = Bed::class,
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
data class Bed(
    @PlanningId
    var id: String = TimebasedUuid.Epoch.nextIdAsString(),
    var room: Room? = null,
    var indexInRoom: Int? = null,
) {
    val roomName: String?
        get() = room?.name
}
