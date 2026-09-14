package timefold.workshop.bed.allocation.solver

import ai.timefold.solver.core.api.score.stream.test.ConstraintVerifier
import ai.timefold.solver.core.api.solver.SolverFactory
import ai.timefold.solver.core.config.solver.EnvironmentMode
import ai.timefold.solver.core.config.solver.SolverConfig
import ai.timefold.solver.core.config.solver.termination.TerminationConfig
import io.bluetape4k.assertions.shouldHaveSize
import io.bluetape4k.assertions.shouldNotBeNull
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
class BedAllocationIncrementalScoreTest(
    @param:Autowired private val solverConfig: SolverConfig,
) {

    private val constraintVerifier: ConstraintVerifier<BedAllocationConstraintProvider, BedPlan> =
        ConstraintVerifier.build(
            BedAllocationConstraintProvider(),
            BedPlan::class.java,
            Stay::class.java,
        )

    @Test
    fun `FULL_ASSERT solver가 same-bed join과 unassigned filter 전이를 검증한다`() {
        val solved = SolverFactory.create<BedPlan>(
            solverConfig.copyConfig()
                .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
                .withRandomSeed(0L)
                .withTerminationConfig(TerminationConfig().withStepCountLimit(64)),
        ).buildSolver().solve(fullAssertFixture())

        solved.score.shouldNotBeNull()
        solved.stays shouldHaveSize 3
    }

    @Test
    fun `same-bed join은 bed assignment 진입과 이탈에서 일관된 match를 만든다`() {
        val bedA = Bed("bed-a")
        val bedB = Bed("bed-b")
        val first = stay("stay-a", Patient("patient-a", "Patient A", Gender.MALE, 40), bedA)
        val second = stay("stay-b", Patient("patient-b", "Patient B", Gender.MALE, 41), bedA)

        constraintVerifier.verifyThat(BedAllocationConstraintProvider::sameBedInSameNight)
            .given(first, second)
            .penalizesBy(6)
        second.bed = bedB
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::sameBedInSameNight)
            .given(first, second)
            .penalizesBy(0)
        second.bed = bedA
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::sameBedInSameNight)
            .given(first, second)
            .penalizesBy(6)
    }

    @Test
    fun `unassigned와 gender filter가 true false true를 통과한다`() {
        val maleOnlyRoom = Room(
            id = "room-male-only",
            name = "Male-only room",
            genderLimitation = GenderLimitation.MALE_ONLY,
        )
        val maleOnlyBed = Bed("bed-male-only", maleOnlyRoom)
        val availableBed = Bed("bed-available", Room("room-available"))
        val stay = stay(
            "stay-female",
            Patient("patient-female", "Female patient", Gender.FEMALE, 42),
            availableBed,
        )

        stay.bed = null
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::assignEveryPatientToABed)
            .given(stay)
            .penalizesBy(6)
        stay.bed = availableBed
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::assignEveryPatientToABed)
            .given(stay)
            .penalizesBy(0)
        stay.bed = null
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::assignEveryPatientToABed)
            .given(stay)
            .penalizesBy(6)

        stay.bed = maleOnlyBed
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::femaleInMaleRoom)
            .given(stay)
            .penalizesBy(6)
        stay.bed = availableBed
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::femaleInMaleRoom)
            .given(stay)
            .penalizesBy(0)
        stay.bed = maleOnlyBed
        constraintVerifier.verifyThat(BedAllocationConstraintProvider::femaleInMaleRoom)
            .given(stay)
            .penalizesBy(6)
    }

    private fun fullAssertFixture(): BedPlan {
        val department = Department("department-1", "General")
        val room = Room("room-1", "Room 1")
        val bedA = Bed("bed-a")
        val bedB = Bed("bed-b")
        room.beds.addAll(listOf(bedA, bedB))
        department.rooms.add(room)

        val stays = mutableListOf(
            stay("stay-a", Patient("patient-a", "Patient A", Gender.MALE, 40), bedA),
            stay("stay-b", Patient("patient-b", "Patient B", Gender.MALE, 41), bedA),
            stay("stay-female", Patient("patient-female", "Female patient", Gender.FEMALE, 42), bedB),
        )

        return BedPlan(
            departments = mutableListOf(department),
            stays = stays,
        )
    }

    private fun stay(id: String, patient: Patient, bed: Bed?): Stay = Stay(
        id = id,
        patient = patient,
        arrivalDate = ARRIVAL,
        departureDate = DEPARTURE,
        specialty = "general",
        bed = bed,
    )

    private companion object {
        val ARRIVAL: LocalDate = LocalDate.of(2026, 1, 1)
        val DEPARTURE: LocalDate = ARRIVAL.plusDays(5)
    }
}
