package timefold.workshop.bed.allocation.domain

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
    fun `Bed - json mapping`(@RandomValue bed: Bed) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bed)
        log.debug { "Bed json: $json" }
        val deserializedBed = mapper.readValue<Bed>(json)
        deserializedBed shouldBeEqualTo bed
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `Room - json mapping`(@RandomValue room: Room) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(room)
        log.debug { "Room json: $json" }
        val deserializedRoom = mapper.readValue<Room>(json)
        deserializedRoom shouldBeEqualTo room
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `Department - json mapping`(@RandomValue dept: Department) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dept)
        log.debug { "Dept json: $json" }
        val deserializedDept = mapper.readValue<Department>(json)
        deserializedDept shouldBeEqualTo dept
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `Stay - json mapping`(@RandomValue stay: Stay) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(stay)
        log.debug { "Stay json: $json" }
        val deserializedStay = mapper.readValue<Stay>(json)
        deserializedStay shouldBeEqualTo stay
    }

    @RepeatedTest(REPEAT_SIZE)
    fun `BedPlan - json mapping`(@RandomValue bedPlan: BedPlan) {
        val json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bedPlan)
        log.debug { "BedPlan json: $json" }
        val deserializedBedPlan = mapper.readValue<BedPlan>(json)
        deserializedBedPlan shouldBeEqualTo bedPlan
    }
}
