package timefold.workshop.school.timetabling.solver

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier
import io.bluetape4k.logging.coroutines.KLoggingChannel
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
class TimetableConstraintProviderTest(
    @param:Autowired private val constraintVerifier: ConstraintVerifier<TimetableConstraintProvider, Timetable>,
) {

    companion object: KLoggingChannel() {
        private val ROOM1 = Room("1", "Room1")
        private val ROOM2 = Room("2", "Room2")

        private val TIMESLOT1 = Timeslot("1", DayOfWeek.MONDAY, LocalTime.NOON)
        private val TIMESLOT2 = Timeslot("2", DayOfWeek.TUESDAY, LocalTime.NOON)
        private val TIMESLOT3 = Timeslot("3", DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(1))
        private val TIMESLOT4 = Timeslot("4", DayOfWeek.TUESDAY, LocalTime.NOON.plusHours(3))
    }

    @Test
    fun `room conflit`() {
        val firstLesson = Lesson("1", "Subject1", "Teacher1", "Group1", TIMESLOT1, ROOM1)
        val conflictingLesson = Lesson("2", "Subject2", "Teacher2", "Group2", TIMESLOT1, ROOM1)
        val nonConflictingLesson = Lesson("3", "Subject3", "Teacher3", "Group3", TIMESLOT2, ROOM1)

        constraintVerifier.verifyThat(TimetableConstraintProvider::roomConflict)
            .given(firstLesson, conflictingLesson, nonConflictingLesson)
            .penalizesBy(1)
    }

    @Test
    fun `teacher conflict`() {
        val conflictingTeacher = "Teacher1"
        val firstLesson = Lesson("1", "Subject1", conflictingTeacher, "Group1", TIMESLOT1, ROOM1)
        val conflictingLesson = Lesson("2", "Subject2", conflictingTeacher, "Group2", TIMESLOT1, ROOM2)
        val nonConflictingLesson = Lesson("3", "Subject3", "Teacher2", "Group3", TIMESLOT2, ROOM1)

        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherConflict)
            .given(firstLesson, conflictingLesson, nonConflictingLesson)
            .penalizesBy(1)
    }

    @Test
    fun `student group conflict`() {
        val conflictingGroup = "Group1"
        val firstLesson = Lesson("1", "Subject1", "Teacher1", conflictingGroup, TIMESLOT1, ROOM1)
        val conflictingLesson = Lesson("2", "Subject2", "Teacher2", conflictingGroup, TIMESLOT1, ROOM2)
        val nonConflictingLesson = Lesson("3", "Subject3", "Teacher3", "Group3", TIMESLOT2, ROOM1)

        constraintVerifier.verifyThat(TimetableConstraintProvider::studentGroupConflict)
            .given(firstLesson, conflictingLesson, nonConflictingLesson)
            .penalizesBy(1)
    }

    @Test
    fun `teacher room stability`() {
        val teacher = "Teacher1"
        val lessonInFirstRoom = Lesson("1", "Subject1", teacher, "Group1", TIMESLOT1, ROOM1)
        val lessonInSameRoom = Lesson("2", "Subject2", teacher, "Group2", TIMESLOT1, ROOM1)
        val lessonInDifferentRoom = Lesson("3", "Subject3", teacher, "Group3", TIMESLOT1, ROOM2)

        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherRoomStability)
            .given(lessonInFirstRoom, lessonInDifferentRoom, lessonInSameRoom)
            .penalizesBy(2)
    }

    @Test
    fun `teacher time efficiency`() {
        val teacher = "Teacher1"
        val singleLessonOnMonday = Lesson("1", "Subject1", teacher, "Group1", TIMESLOT1, ROOM1)
        val firstTuesdayLesson = Lesson("2", "Subject2", teacher, "Group2", TIMESLOT2, ROOM1)
        val secondTuesdayLesson = Lesson("3", "Subject3", teacher, "Group3", TIMESLOT3, ROOM1)
        val thirdTuesdayLessonWithGap = Lesson("4", "Subject4", teacher, "Group4", TIMESLOT4, ROOM1)
        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherTimeEfficiency)
            .given(singleLessonOnMonday, firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLessonWithGap)
            .rewardsWith(1) // Second tuesday lesson immediately follows the first.

        // Reverse ID order
        val altSecondTuesdayLesson = Lesson("2", "Subject2", teacher, "Group3", TIMESLOT3, ROOM1)
        val altFirstTuesdayLesson = Lesson("3", "Subject3", teacher, "Group2", TIMESLOT2, ROOM1)
        constraintVerifier.verifyThat(TimetableConstraintProvider::teacherTimeEfficiency)
            .given(altSecondTuesdayLesson, altFirstTuesdayLesson)
            .rewardsWith(1) // Second tuesday lesson immediately follows the first.
    }

    @Test
    fun `학생들은 교과목의 다양성을 가져야 합니다`() {
        val studentGroup = "Group1"
        val repeatedSubject = "Subject1"
        val mondayLesson = Lesson("1", repeatedSubject, "Teacher1", studentGroup, TIMESLOT1, ROOM1)
        val firstTuesdayLesson = Lesson("2", repeatedSubject, "Teacher2", studentGroup, TIMESLOT2, ROOM1)
        val secondTuesdayLesson = Lesson("3", repeatedSubject, "Teacher3", studentGroup, TIMESLOT3, ROOM1)
        val thirdTuesdayLessonWithDifferentSubject = Lesson("4", "Subject2", "Teacher4", studentGroup, TIMESLOT4, ROOM1)
        val lessonInAnotherGroup = Lesson("5", repeatedSubject, "Teacher5", "Group2", TIMESLOT1, ROOM1)
        constraintVerifier.verifyThat(TimetableConstraintProvider::studentGroupSubjectVariety)
            .given(
                mondayLesson, firstTuesdayLesson, secondTuesdayLesson, thirdTuesdayLessonWithDifferentSubject,
                lessonInAnotherGroup
            )
            .penalizesBy(1) // Second tuesday lesson immediately follows the first.
    }
}
