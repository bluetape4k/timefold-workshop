package timefold.workshop.school.timetabling.solver.justifications

import ai.timefold.solver.core.api.score.stream.ConstraintJustification
import timefold.workshop.school.timetabling.domain.Lesson

data class TeacherRoomStabilityJustification(
    val teacher: String,
    val lesson1: Lesson,
    val lesson2: Lesson,
    val description: String = getDescription(teacher, lesson1, lesson2),
): ConstraintJustification {

    companion object {
        fun getDescription(
            teacher: String,
            lesson1: Lesson,
            lesson2: Lesson,
        ): String =
            "Teacher '$teacher' has two lessons in different rooms: " +
                    "room '${lesson1.room}' at '${lesson1.timeslot?.dayOfWeek} ${lesson1.timeslot?.startTime}' " +
                    "and room '${lesson2.room}' at '${lesson2.timeslot?.dayOfWeek} ${lesson2.timeslot?.startTime}'"
    }
}
