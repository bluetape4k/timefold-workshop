package timefold.workshop.school.timetabling.domain

import io.bluetape4k.idgenerators.uuid.TimebasedUuid
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.time.DayOfWeek
import java.time.LocalTime

object TimetableProvider {

    enum class DataSizeType {
        SMALL,
        LARGE
    }

    suspend fun generateTimetable(dataSizeType: DataSizeType): Timetable = coroutineScope {
        val timeslots = async { generateTimeslots(dataSizeType) }
        val rooms = async { generateRooms(dataSizeType) }
        val lessons = async { generateLessons(dataSizeType) }

        Timetable(
            name = dataSizeType.name,
            timeslots = timeslots.await(),
            rooms = rooms.await(),
            lessons = lessons.await(),
        )
    }

    private fun generateTimeslots(
        dayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        startHours: IntArray = intArrayOf(8, 9, 10, 13, 14),
    ): List<Timeslot> {
        return startHours
            .map {
                val slotId = TimebasedUuid.Epoch.nextIdAsString()
                val startTime = LocalTime.of(it, 30)
                Timeslot(
                    id = slotId,
                    dayOfWeek = dayOfWeek,
                    startTime = startTime.plusHours(1),
                    endTime = startTime.plusHours(1)
                )
            }
    }

    suspend fun generateTimeslots(dataSizeType: DataSizeType): List<Timeslot> {
        val timeslots = mutableListOf<Timeslot>()
        DayOfWeek.entries
            .filter { it in listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY) }
            .forEach {
                generateTimeslots(dayOfWeek = it, startHours = intArrayOf(8, 9, 10, 13, 14))
                    .apply { timeslots.addAll(this) }
            }
        if (dataSizeType == DataSizeType.LARGE) {
            DayOfWeek.entries
                .filterNot { it == DayOfWeek.SATURDAY || it == DayOfWeek.SUNDAY }
                .forEach {
                    generateTimeslots(dayOfWeek = it, startHours = intArrayOf(8, 9, 10, 13, 14))
                        .apply { timeslots.addAll(this) }
                }
        }
        return timeslots
    }

    suspend fun generateRooms(dataSizeType: DataSizeType): List<Room> {
        val rooms = mutableListOf<Room>()

        rooms.addAll(
            listOf(
                Room(id = generateUniqueId(), name = "Room A"),
                Room(id = generateUniqueId(), name = "Room B"),
                Room(id = generateUniqueId(), name = "Room C")
            )
        )
        if (dataSizeType == DataSizeType.LARGE) {
            rooms.addAll(
                listOf(
                    Room(id = generateUniqueId(), name = "Room D"),
                    Room(id = generateUniqueId(), name = "Room E"),
                    Room(id = generateUniqueId(), name = "Room F")
                )
            )
        }
        return rooms
    }

    suspend fun generateLessons(dataSizeType: DataSizeType): List<Lesson> {
        val lessons = mutableListOf<Lesson>()

        lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "Chemistry", "M. Curie", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "Biology", "C. Darwin", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "English", "I. Jones", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "English", "I. Jones", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "Spanish", "P. Cruz", "9th grade"))
        lessons.add(Lesson(generateUniqueId(), "Spanish", "P. Cruz", "9th grade"))

        if (dataSizeType == DataSizeType.LARGE) {
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "ICT", "A. Turing", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Geography", "C. Darwin", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Geology", "C. Darwin", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "I. Jones", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Drama", "I. Jones", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "9th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "9th grade"))
        }

        lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "Chemistry", "M. Curie", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "French", "M. Curie", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "Geography", "C. Darwin", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "10th grade"))
        lessons.add(Lesson(generateUniqueId(), "Spanish", "P. Cruz", "10th grade"))

        if (dataSizeType == DataSizeType.LARGE) {
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "ICT", "A. Turing", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Biology", "C. Darwin", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Geology", "C. Darwin", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Drama", "I. Jones", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "10th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "10th grade"))

            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "ICT", "A. Turing", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Chemistry", "M. Curie", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "French", "M. Curie", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Geography", "C. Darwin", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Biology", "C. Darwin", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Geology", "C. Darwin", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Spanish", "P. Cruz", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Drama", "P. Cruz", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "11th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "11th grade"))

            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Math", "A. Turing", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "ICT", "A. Turing", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Chemistry", "M. Curie", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "French", "M. Curie", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physics", "M. Curie", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Geography", "C. Darwin", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Biology", "C. Darwin", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Geology", "C. Darwin", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "History", "I. Jones", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "English", "P. Cruz", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Spanish", "P. Cruz", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Drama", "P. Cruz", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Art", "S. Dali", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "12th grade"))
            lessons.add(Lesson(generateUniqueId(), "Physical education", "C. Lewis", "12th grade"))
        }

        return lessons
    }

    private fun generateUniqueId(): String =
        TimebasedUuid.Epoch.nextIdAsString()
}
