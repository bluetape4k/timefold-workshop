package timefold.workshop.school.timetabling.solver

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
import timefold.workshop.school.timetabling.domain.Lesson
import timefold.workshop.school.timetabling.domain.Room
import timefold.workshop.school.timetabling.domain.Timeslot
import timefold.workshop.school.timetabling.domain.Timetable
import java.time.DayOfWeek
import java.time.LocalTime

@SpringBootTest
@DisabledInNativeImage
class TimetableIncrementalScoreTest(
    @param:Autowired private val solverConfig: SolverConfig,
) {

    private val constraintVerifier: ConstraintVerifier<TimetableConstraintProvider, Timetable> =
        ConstraintVerifier.build(
            TimetableConstraintProvider(),
            Timetable::class.java,
            Lesson::class.java,
        )

    @Test
    fun `FULL_ASSERT solver가 join과 filter 상태 변화를 검증한다`() {
        val solved = SolverFactory.create<Timetable>(
            solverConfig.copyConfig()
                .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
                .withRandomSeed(0L)
                .withTerminationConfig(TerminationConfig().withStepCountLimit(64)),
        ).buildSolver().solve(fullAssertFixture())

        solved.score.shouldNotBeNull()
        solved.lessons shouldHaveSize 4
    }

    @Test
    fun `teacher room student join은 assignment 진입과 이탈에서 일관된 match를 만든다`() {
        val first = Lesson("join-1", "Math", "Teacher", "Group", MONDAY_MORNING, ROOM_A)
        val second = Lesson("join-2", "Science", "Teacher", "Group", MONDAY_MORNING, ROOM_A)

        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherConflict)
            .given(first, second)
            .penalizesBy(1)
        second.timeslot = TUESDAY_MORNING
        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherConflict)
            .given(first, second)
            .penalizesBy(0)
        second.timeslot = MONDAY_MORNING
        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherConflict)
            .given(first, second)
            .penalizesBy(1)

        second.timeslot = TUESDAY_MORNING
        constraintVerifier.verifyThat(TimetableConstraintProvider::roomConflict)
            .given(first, second)
            .penalizesBy(0)
        second.room = ROOM_B
        constraintVerifier.verifyThat(TimetableConstraintProvider::roomConflict)
            .given(first, second)
            .penalizesBy(0)
        second.timeslot = MONDAY_MORNING
        constraintVerifier.verifyThat(TimetableConstraintProvider::roomConflict)
            .given(first, second)
            .penalizesBy(0)
        second.room = ROOM_A
        constraintVerifier.verifyThat(TimetableConstraintProvider::roomConflict)
            .given(first, second)
            .penalizesBy(1)

        second.timeslot = TUESDAY_MORNING
        constraintVerifier.verifyThat(TimetableConstraintProvider::studentGroupConflict)
            .given(first, second)
            .penalizesBy(0)
        second.timeslot = MONDAY_MORNING
        constraintVerifier.verifyThat(TimetableConstraintProvider::studentGroupConflict)
            .given(first, second)
            .penalizesBy(1)
        second.timeslot = TUESDAY_MORNING
    }

    @Test
    fun `teacher time efficiency와 subject variety filter가 true false true를 통과한다`() {
        val first = Lesson("filter-1", "Math", "Teacher", "Group", MONDAY_MORNING, ROOM_A)
        val second = Lesson("filter-2", "Math", "Teacher", "Group", MONDAY_NEXT, ROOM_A)

        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherTimeEfficiency)
            .given(first, second)
            .rewardsWith(1)
        second.timeslot = TUESDAY_MORNING
        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherTimeEfficiency)
            .given(first, second)
            .rewardsWith(0)
        second.timeslot = MONDAY_NEXT
        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherTimeEfficiency)
            .given(first, second)
            .rewardsWith(1)

        constraintVerifier.verifyThat(TimetableConstraintProvider::studentGroupSubjectVariety)
            .given(first, second)
            .penalizesBy(1)
        second.timeslot = MONDAY_FAR
        constraintVerifier.verifyThat(TimetableConstraintProvider::studentGroupSubjectVariety)
            .given(first, second)
            .penalizesBy(0)
        second.timeslot = MONDAY_NEXT
        constraintVerifier.verifyThat(TimetableConstraintProvider::studentGroupSubjectVariety)
            .given(first, second)
            .penalizesBy(1)
    }

    private fun fullAssertFixture(): Timetable = Timetable(
        name = "incremental-school",
        timeslots = listOf(MONDAY_MORNING, MONDAY_NEXT, TUESDAY_MORNING, MONDAY_FAR),
        rooms = listOf(ROOM_A, ROOM_B),
        lessons = listOf(
            Lesson("fixture-1", "Math", "Teacher A", "Group A", MONDAY_MORNING, ROOM_A),
            Lesson("fixture-2", "Science", "Teacher A", "Group B", MONDAY_MORNING, ROOM_A),
            Lesson("fixture-3", "History", "Teacher B", "Group A", MONDAY_NEXT, ROOM_B),
            Lesson("fixture-4", "Math", "Teacher A", "Group A", TUESDAY_MORNING, ROOM_A),
        ),
    )

    private companion object {
        val ROOM_A = Room("room-a", "Room A")
        val ROOM_B = Room("room-b", "Room B")

        val MONDAY_MORNING = Timeslot(
            "monday-morning",
            DayOfWeek.MONDAY,
            LocalTime.of(9, 0),
            LocalTime.of(9, 50),
        )
        val MONDAY_NEXT = Timeslot(
            "monday-next",
            DayOfWeek.MONDAY,
            LocalTime.of(10, 0),
            LocalTime.of(10, 50),
        )
        val MONDAY_FAR = Timeslot(
            "monday-far",
            DayOfWeek.MONDAY,
            LocalTime.of(12, 0),
            LocalTime.of(12, 50),
        )
        val TUESDAY_MORNING = Timeslot(
            "tuesday-morning",
            DayOfWeek.TUESDAY,
            LocalTime.of(9, 0),
            LocalTime.of(9, 50),
        )
    }
}
