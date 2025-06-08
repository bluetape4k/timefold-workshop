package timefold.workshop.school.timetabling.domain

import com.fasterxml.jackson.module.kotlin.readValue
import io.bluetape4k.jackson.Jackson
import io.bluetape4k.junit5.random.RandomValue
import io.bluetape4k.junit5.random.RandomizedTest
import io.bluetape4k.logging.coroutines.KLoggingChannel
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest

@RandomizedTest
class JsonMappingTest {

    companion object: KLoggingChannel() {
        private const val REPEAT_SIZE = 5
    }

    private val mapper by lazy { Jackson.defaultJsonMapper }

    @RepeatedTest(REPEAT_SIZE)
    fun `Lesson - json mapping`(@RandomValue lesson: Lesson) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lesson)
        log.debug { "Lesson json: $json" }
        val deserialized = mapper.readValue<Lesson>(json)
        deserialized shouldBeEqualTo lesson
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `Room - json mapping`(@RandomValue room: Room) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(room)
        log.debug { "Room json: $json" }
        val deserialized = mapper.readValue<Room>(json)
        deserialized shouldBeEqualTo room
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `Timeslot - json mapping`(@RandomValue timeslot: Timeslot) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(timeslot)
        log.debug { "Timeslot json: $json" }
        val deserialized = mapper.readValue<Timeslot>(json)
        deserialized shouldBeEqualTo timeslot
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `Timetable - json mapping`(@RandomValue timetable: Timetable) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(timetable)
        log.debug { "Timetable json: $json" }
        val deserialized = mapper.readValue<Timetable>(json)
        deserialized shouldBeEqualTo timetable
    }
}
