package timefold.workshop.school.timetabling.solver.justifications

import timefold.workshop.school.timetabling.domain.Lesson
import java.io.Serializable

data class StudentGroupSubjectVarietyJustification(
    val studentGroup: String,
    val lesson1: Lesson,
    val lesson2: Lesson,
    val description: String = getDescription(studentGroup, lesson1, lesson2),
): Serializable {

    companion object {
        fun getDescription(
            studentGroup: String,
            lesson1: Lesson,
            lesson2: Lesson,
        ): String =
            "Student Group '$studentGroup' has two consecutive lessons on '${lesson1.subject}'" +
                    " at '${lesson1.timeslot?.dayOfWeek} ${lesson1.timeslot?.startTime}'" +
                    " and at '${lesson2.timeslot?.dayOfWeek} ${lesson2.timeslot?.startTime}'"
    }
}
