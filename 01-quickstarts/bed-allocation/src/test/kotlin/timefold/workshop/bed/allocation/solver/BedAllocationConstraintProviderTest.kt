package timefold.workshop.bed.allocation.solver

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier
import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledInNativeImage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import timefold.workshop.bed.allocation.domain.Bed
import timefold.workshop.bed.allocation.domain.BedPlan
import timefold.workshop.bed.allocation.domain.Department
import timefold.workshop.bed.allocation.domain.Gender
import timefold.workshop.bed.allocation.domain.GenderLimitation
import timefold.workshop.bed.allocation.domain.Patient
import timefold.workshop.bed.allocation.domain.Room
import timefold.workshop.bed.allocation.domain.Stay
import java.time.LocalDate

@SpringBootTest
@DisabledInNativeImage
class BedAllocationConstraintProviderTest(
    @Autowired private val constraintVerifier: ConstraintVerifier<BedAllocationConstraintProvider, BedPlan>,
) {

    companion object: KLoggingChannel() {
        private val ZERO_NIGHT = LocalDate.of(2021, 2, 1)
        private val FIVE_NIGHT = ZERO_NIGHT.plusDays(5)

        private const val DEFAULT_SPECIALTY = "default"
    }

    @Test
    fun `context loading`() {
        constraintVerifier.shouldNotBeNull()
    }

    @Test
    fun `여성 환자가 남성 전용 병실에 배정되지 않도록 한다`() {
        val room = Room().apply {
            genderLimitation = GenderLimitation.MALE_ONLY
        }
        val bed = Bed().apply { this.room = room }

        val genderAdmission = Stay().apply {
            this.id = "0"
            this.arrivalDate = ZERO_NIGHT
            this.departureDate = FIVE_NIGHT
            this.specialty = DEFAULT_SPECIALTY
            this.bed = bed
        }
        genderAdmission.patient = Patient(
            id = "patient1",
            name = "여성 환자",
            gender = Gender.FEMALE
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::femaleInMaleRoom)
            .given(genderAdmission)
            .penalizesBy(6)
    }

    @Test
    fun `남성 환자가 여성 전용 병실에 배정되지 않도록 한다`() {
        val room = Room().apply {
            genderLimitation = GenderLimitation.FEMALE_ONLY
        }
        val bed = Bed().apply { this.room = room }

        val genderAdmission = Stay().apply {
            this.id = "0"
            this.arrivalDate = ZERO_NIGHT
            this.departureDate = FIVE_NIGHT
            this.specialty = DEFAULT_SPECIALTY
            this.bed = bed
        }
        genderAdmission.patient = Patient(
            id = "patient1",
            name = "남성 환자",
            gender = Gender.MALE,
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::maleInFemaleRoom)
            .given(genderAdmission)
            .penalizesBy(6)
    }

    @Test
    fun `동일한 병실 침대에 같은 날짜에 여러 환자가 배정되지 않도록 한다`() {
        val bed = Bed("1")
        val stay = Stay().apply {
            this.id = "0"
            this.arrivalDate = ZERO_NIGHT
            this.departureDate = FIVE_NIGHT
            this.specialty = DEFAULT_SPECIALTY
            this.bed = bed
        }
        val sameBedAndNightStay = Stay().apply {
            this.id = "2"
            this.arrivalDate = ZERO_NIGHT
            this.departureDate = FIVE_NIGHT
            this.specialty = DEFAULT_SPECIALTY
            this.bed = bed
        }

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::sameBedInSameNight)
            .given(stay, sameBedAndNightStay)
            .penalizesBy(6)
    }

    @Test
    fun `성인 병동의 최소 나이`() {
        val department = Department("1", "Adult department", minimumAge = 18)
        val room = Room("1", department = department)
        val bed = Bed(room = room)

        val patient = Patient(
            id = "patient1",
            name = "어린이 환자",
            age = 5,
        )
        val admission = Stay(
            "0",
            patient = patient,
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = bed
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::departmentMinimumAge)
            .given(admission, department)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }

    @Test
    fun `성인 병동의 최대 나이`() {
        val department = Department("2", "Adult department", maximumAge = 18)
        val room = Room("1", department = department)
        val bed = Bed(room = room)

        val patient = Patient(
            id = "patient1",
            name = "중년 환자",
            age = 42,
        )

        val admission = Stay(
            "0",
            patient = patient,
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = bed
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::departmentMaximumAge)
            .given(admission, department)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }

    @Test
    fun `필요 환자 장비가 구비되었는가`() {
        val room = Room(
            id = "room1",
            name = "장비가 있는 병실",
            equipments = mutableListOf("TELEMETRY")
        )
        val bed = Bed(id = "bed1", room = room)

        val admission = Stay(
            id = "stay1",
            patient = Patient(id = "patient1", name = "환자1"),
            patientRequiredEquipments = mutableListOf("TELEVISION", "TELEMETRY"),
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = bed
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::requiredPatientEquipment)
            .given(admission)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }

    @Test
    fun `같은 날짜에 동일 성별 병실에 다른 성별의 환자가 배정되지 않도록 한다`() {
        val room = Room("1", genderLimitation = GenderLimitation.SAME_GENDER)

        // Assign female
        val bed1 = Bed("bed1", room = room)

        val stay = Stay(
            id = "stay1",
            patient = Patient(id = "patient1", name = "환자 1", gender = Gender.FEMALE),
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = bed1
        )

        val bed2 = Bed("bed2", room = room)
        val stay2 = Stay(
            id = "stay2",
            patient = Patient(id = "patient2", name = "환자 2", gender = Gender.MALE),
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = bed2
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::differentGenderInSameGenderRoomIfSameNight)
            .given(stay, stay2)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }

    @Test
    fun `모든 환자가 각 베드에 배정되는가`() {
        val stay = Stay(
            id = "0",
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = null
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::assignEveryPatientToABed)
            .given(stay)
            .penalizesBy(6)
    }

    @Test
    fun `선호하는 병실의 최대 여유분`() {
        val room = Room(capacity = 6)
        val assignedBedInExceedCapacity = Bed(room = room)

        val stay = Stay(
            id = "0",
            patientPreferredMaximumCapacity = 3,
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = assignedBedInExceedCapacity,
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::preferredMaximumCapacity)
            .given(stay)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }

    @Test
    fun `선호하는 환자용 장비`() {
        val room = Room(equipments = mutableListOf("TELEMETRY"))
        val bed = Bed(room = room)

        val stay = Stay(
            id = "0",
            patientPreferredEquipments = mutableListOf("TELEVISION", "TELEMETRY"),
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = DEFAULT_SPECIALTY,
            bed = bed,
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::preferredPatientEquipment)
            .given(stay)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }

    @Test
    fun `병동의 전문 분야가 아닌 경우에는 penalty 를 받는다`() {
        val department = Department(
            id = "1",
            name = "내과",
            specialtyToPriority = mutableMapOf("spec1" to 1)
        )
        val roomInDep = Room(department = department)
        val bedInRoomInDep = Bed(room = roomInDep)

        val spec1 = "spec1"

        val staySpec1 = Stay(
            id = "0",
            patientPreferredMaximumCapacity = 3,
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = spec1,
            bed = bedInRoomInDep,
        )

        val spec2 = "spec2"

        val staySpec2 = Stay(
            id = "1",
            patientPreferredMaximumCapacity = 3,
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = spec2,
            bed = bedInRoomInDep,
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::departmentSpecialty)
            .given(staySpec1, staySpec2)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }

    @Test
    fun `병동의 전문분야가 첫번째 우선순위가 아니다`() {
        val department = Department(id = "dept1", specialtyToPriority = mutableMapOf("spec1" to 2, "spec2" to 1))
        val roomInDep = Room(id = "room1", department = department)
        department.addRoom(roomInDep)

        val bedInDep = Bed(id = "bed1", room = roomInDep)

        // 첫 번째 우선순위
        val spec1 = "spec1"
        val stay1 = Stay(
            id = "stay1",
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = spec1,
            bed = bedInDep,
        )
        // 두 번째 우선순위
        val spec2 = "spec2"
        val stay2 = Stay(
            id = "stay2",
            arrivalDate = ZERO_NIGHT,
            departureDate = FIVE_NIGHT,
            specialty = spec2,
            bed = bedInDep,
        )

        constraintVerifier
            .verifyThat(BedAllocationConstraintProvider::departmentSpecialtyNotFirstPriority)
            .given(stay1, stay2, department)
            .penalizesBy(6) // 6 nights * 1 penalty per night
    }
}
