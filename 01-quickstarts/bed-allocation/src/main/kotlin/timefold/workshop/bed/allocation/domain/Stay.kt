package timefold.workshop.bed.allocation.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.lookup.PlanningId
import ai.timefold.solver.core.api.domain.variable.PlanningVariable
import com.fasterxml.jackson.annotation.JsonIgnore
import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import java.io.Serializable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@PlanningEntity
class Stay: Serializable {

    companion object {
        operator fun invoke(
            id: String = TimebasedUuid.Epoch.nextIdAsString(),
            patient: Patient? = null,
            patientPreferredMaximumCapacity: Int? = null,
            patientRequiredEquipments: List<String> = emptyList(),
            patientPreferredEquipments: List<String> = emptyList(),
            arrivalDate: LocalDate? = null,
            departureDate: LocalDate? = null,
            specialty: String? = null,
            bed: Bed? = null,
        ): Stay {
            return Stay().apply {
                this.id = id
                this.patient = patient
                this.patientPreferredMaximumCapacity = patientPreferredMaximumCapacity
                this.patientRequiredEquipments.addAll(patientRequiredEquipments)
                this.patientPreferredEquipments.addAll(patientPreferredEquipments)
                this.arrivalDate = arrivalDate
                this.departureDate = departureDate
                this.specialty = specialty
                this.bed = bed
            }
        }
    }

    @PlanningId
    var id: String = ""

    var patient: Patient? = null

    var patientPreferredMaximumCapacity: Int? = null
    val patientRequiredEquipments: MutableList<String> = mutableListOf()
    val patientPreferredEquipments: MutableList<String> = mutableListOf()

    var arrivalDate: LocalDate? = null
    var departureDate: LocalDate? = null

    var specialty: String? = null

    @PlanningVariable(allowsUnassigned = true)
    var bed: Bed? = null


    @get:JsonIgnore
    val nightCount: Int
        get() = ChronoUnit.DAYS.between(arrivalDate, departureDate).toInt() + 1

    fun calculateSameNightCount(other: Stay): Int {
        val maxArrivalDate = maxOf(arrivalDate!!, other.arrivalDate!!)
        val minDepartureDate = minOf(departureDate!!, other.departureDate!!)

        return maxOf(
            0,
            ChronoUnit.DAYS.between(maxArrivalDate, minDepartureDate).toInt() + 1
        )
    }

    @get:JsonIgnore
    val hasDepartmentSpecialty: Boolean
        get() = department?.specialtyToPriority?.containsKey(specialty) ?: false

    @get:JsonIgnore
    val specialtyPriority: Int
        get() = department?.specialtyToPriority?.get(specialty) ?: 0

    @get:JsonIgnore
    val room: Room? get() = bed?.room

    @get:JsonIgnore
    val roomCapacity: Int? get() = room?.capacity

    @get:JsonIgnore
    val department: Department? get() = room?.department

    @get:JsonIgnore
    val roomGenderLimitation: GenderLimitation?
        get() = room?.genderLimitation

    @get:JsonIgnore
    val patientAge: Int get() = patient?.age ?: 0

    override fun equals(other: Any?): Boolean =
        other is Stay && id == other.id

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String {
        return "${patient?.name} ($arrivalDate - $departureDate)"
    }
}
