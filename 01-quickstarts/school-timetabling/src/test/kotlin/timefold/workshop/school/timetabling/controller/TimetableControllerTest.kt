package timefold.workshop.school.timetabling.controller

import ai.timefold.solver.core.api.solver.SolverStatus
import io.bluetape4k.logging.debug
import io.bluetape4k.spring.tests.httpGet
import io.bluetape4k.spring.tests.httpPost
import io.bluetape4k.spring.tests.httpPut
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeNull
import org.awaitility.kotlin.atMost
import org.awaitility.kotlin.await
import org.awaitility.kotlin.withPollInterval
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import timefold.workshop.school.timetabling.AbstractSchoolTimetablingTest
import timefold.workshop.school.timetabling.domain.Timetable
import timefold.workshop.school.timetabling.domain.TimetableProvider
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(
    properties = [
        "spring.main.web-application-type=reactive",
        "spring.main.lazy-initialization=true",
        "spring.main.allow-bean-definition-overriding=true",
        "spring.main.banner-mode=off",
        "spring.main.cloud-platform=none",
        "timefold.benchmark.solver.termination.spent-limit=1h",
        "timefold.solver.termination.best-score-limit=0hard/*soft",
//        "timefold.benchmark.solver.environment-mode=FULL_ASSERT",
//        "timefold.benchmark.solver.move-thread-count=1",
//        "timefold.benchmark.solver.solution-class=timefold.workshop.school.timetabling.domain.Timetable",
//        "timefold.benchmark.solver.entity-class-list=timefold.workshop.school.timetabling.domain.Lesson,timefold.workshop.school.timetabling.domain.Room,timefold.workshop.school.timetabling.domain.Timeslot",
    ],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class TimetableControllerTest(
    @param:Autowired private val testClient: WebTestClient,
): AbstractSchoolTimetablingTest() {

    private val client: WebTestClient
        get() = testClient.mutate()
            .codecs { configurer ->
                configurer.defaultCodecs().enableLoggingRequestDetails(true)
                configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024) // 16 MB
            }
            .build()

    @ParameterizedTest(name = "{0}")
    @EnumSource(TimetableProvider.DataSizeType::class)
    fun solve(dataSizeType: TimetableProvider.DataSizeType) = runTest(timeout = 2.minutes) {
        val testTimetable = retrieveTimetable(dataSizeType)

        val jobId = client.httpPost("/timetables", value = testTimetable)
            .returnResult<String>().responseBody
            .awaitSingle()

        log.debug { "Job ID: $jobId" }

        await
            .atMost(1.minutes)
            .withPollInterval(1.seconds)
            .until {
                val timetable = runBlocking {
                    client.httpGet("/timetables/$jobId/status")
                        .returnResult<Timetable>().responseBody
                        .awaitSingle()
                }

                log.debug { "Solver status: ${timetable.solverStatus}" }
                timetable.solverStatus == SolverStatus.NOT_SOLVING
            }

        val resultTimetable = client.httpGet("/timetables/$jobId")
            .returnResult<Timetable>().responseBody
            .awaitSingle()

        log.debug { "Result timetable score: ${resultTimetable.score}, solverStatus: ${resultTimetable.solverStatus}" }
        resultTimetable.score.shouldNotBeNull()
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(TimetableProvider.DataSizeType::class)
    fun `analyze timetable`(dataSizeType: TimetableProvider.DataSizeType) = runTest(timeout = 5.minutes) {
        val testTimetable = retrieveTimetable(dataSizeType)

        val rooms = testTimetable.rooms
        val timeslots = testTimetable.timeslots

        testTimetable.lessons.forEachIndexed { index, lesson ->
            lesson.room = rooms[index % rooms.size]
            lesson.timeslot = timeslots[index % timeslots.size]
        }

        val analysis = client.httpPut("/timetables/analyze", value = testTimetable)
            .returnResult<String>().responseBody
            .asFlow().toList()
            .joinToString("")

        log.debug { "Score analysis: $analysis" }
        analysis shouldContain "\"score\":"

        val shallowAnalysis = client.httpPut("/timetables/analyze?fetchPolicy=FETCH_SHALLOW", value = testTimetable)
            .returnResult<String>().responseBody
            .asFlow().toList()
            .joinToString("")

        log.debug { "Shallow score analysis: $shallowAnalysis" }
        shallowAnalysis shouldContain "\"score\":"
    }

    private suspend fun retrieveTimetable(dataSizeType: TimetableProvider.DataSizeType): Timetable {
        return client.httpGet("/demo-data/${dataSizeType.name.uppercase()}")
            .returnResult<Timetable>().responseBody
            .awaitSingle()
    }
}
