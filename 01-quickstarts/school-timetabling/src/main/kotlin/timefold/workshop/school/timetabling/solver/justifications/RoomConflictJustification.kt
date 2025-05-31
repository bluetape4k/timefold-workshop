package timefold.workshop.school.timetabling.solver.justifications

import ai.timefold.solver.core.api.score.stream.ConstraintJustification
import timefold.workshop.school.timetabling.domain.Lesson
import timefold.workshop.school.timetabling.domain.Room
import java.io.Serializable

data class RoomConflictJustification(
    val room: Room,
    val lesson1: Lesson,
    val lesson2: Lesson,
    val description: String = getDescription(room, lesson1, lesson2),
): ConstraintJustification, Serializable {

    companion object {
        fun getDescription(
            room: Room,
            lesson1: Lesson,
            lesson2: Lesson,
        ): String =
            "Room '$room' is used" +
                    " for lesson '${lesson1.subject}' for student group '${lesson1.studentGroup}'" +
                    " and lesson '${lesson2.subject}' for student group '${lesson2.studentGroup}'" +
                    " at '${lesson1.timeslot?.dayOfWeek} ${lesson1.timeslot?.startTime}'"
    }
}
