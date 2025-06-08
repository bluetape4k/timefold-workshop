package timefold.workshop.bed.allocation.domain

import ai.timefold.solver.core.api.domain.lookup.PlanningId
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import java.io.Serializable
import java.util.*

/**
 * 병원의 병동([Department])을 나타냅니다. 병동은 여러 병실([Room})을 포함하며, 병동의 이름과 병실 목록을 포함합니다.
 */
@JsonIdentityInfo(
    scope = Department::class,
    generator = ObjectIdGenerators.PropertyGenerator::class,
    property = "id"
)
data class Department(
    @PlanningId
    var id: String = TimebasedUuid.Epoch.nextIdAsString(),
    var name: String = "department-$id",
    val specialtyToPriority: MutableMap<String, Int> = mutableMapOf(),
    var minimumAge: Int = Int.MIN_VALUE,
    var maximumAge: Int = Int.MAX_VALUE,
    val rooms: MutableList<Room> = LinkedList(),
): Serializable {

    fun addRoom(room: Room) {
        if (!rooms.contains(room)) {
            rooms.add(room)
            room.department = this
        }
    }

    fun countHardDisallowedStay(stay: Stay): Int {
        return countDisallowedPatientAge(stay.patient?.age ?: 0)
    }

    fun countDisallowedPatientAge(patientAge: Int): Int {
        var count = 0
        if (patientAge < minimumAge) {
            count++
        }
        if (patientAge > maximumAge) {
            count++
        }
        return count
    }
}
