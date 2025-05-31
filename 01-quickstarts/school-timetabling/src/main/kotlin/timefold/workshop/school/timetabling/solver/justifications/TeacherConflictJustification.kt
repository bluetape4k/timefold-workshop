package timefold.workshop.school.timetabling.solver.justifications

import ai.timefold.solver.core.api.score.stream.ConstraintJustification
import timefold.workshop.school.timetabling.domain.Lesson
import java.io.Serializable

data class TeacherConflictJustification(
    val teacher: String,
    val lesson1: Lesson,
    val lesson2: Lesson,
    val description: String = getDescription(teacher, lesson1, lesson2),
): ConstraintJustification, Serializable {

    companion object {
        fun getDescription(
            teacher: String,
            lesson1: Lesson,
            lesson2: Lesson,
        ): String =
            "Teacher '$teacher' needs to teach" +
                    " lesson '${lesson1.subject}' for student group '${lesson1.studentGroup}' " +
                    " and lesson '${lesson2.subject}' for student group '${lesson2.studentGroup}' " +
                    " at '${lesson1.timeslot?.dayOfWeek} ${lesson1.timeslot?.startTime}'"
    }
}
