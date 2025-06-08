package timefold.workshop.bed.allocation.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.bluetape4k.AbstractValueObject
import io.bluetape4k.idgenerators.uuid.TimebasedUuid

/**
 * 병원의 병실([Room]) 나타내는 클래스입니다. 병상([Bed])를 포함하고 있으며, 병실의 이름, 병과, 수용 인원, 성별 제한 등을 포함합니다.
 */
@JsonIdentityInfo(
    scope = Room::class,
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
data class Room(
    @PlanningId
    var id: String = TimebasedUuid.Epoch.nextIdAsString(),
    var name: String = "room-$id",
    var department: Department? = null,
    var capacity: Int? = null,
    var genderLimitation: GenderLimitation? = null,
    val equipments: MutableList<String> = mutableListOf(),
    val beds: MutableList<Bed> = mutableListOf(),
): AbstractValueObject() {

    fun addBed(bed: Bed) {
        if (!beds.contains(bed)) {
            beds.add(bed)
            bed.room = this
        }
    }
}
