package timefold.workshop.school.timetabling.solver.justifications

import ai.timefold.solver.core.api.score.stream.ConstraintJustification
import timefold.workshop.school.timetabling.domain.Lesson
import java.io.Serializable

data class StudentGroupConflictJustification(
    val studentGroup: String,
    val lesson1: Lesson,
    val lesson2: Lesson,
    val description: String = getDescription(studentGroup, lesson1, lesson2),
): ConstraintJustification, Serializable {
    companion object {
        fun getDescription(
            studentGroup: String,
            lesson1: Lesson,
            lesson2: Lesson,
        ): String =
            "Student group '$studentGroup'" +
                    " has lesson '${lesson1.subject}'" +
                    " and lesson '${lesson2.subject}'" +
                    " at '${lesson1.timeslot?.dayOfWeek} ${lesson1.timeslot?.startTime}'"
    }
}
