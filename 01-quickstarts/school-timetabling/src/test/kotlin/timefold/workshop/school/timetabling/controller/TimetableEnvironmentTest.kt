package timefold.workshop.school.timetabling.controller

import ai.timefold.solver.core.api.solver.SolverFactory
import ai.timefold.solver.core.config.solver.EnvironmentMode
import ai.timefold.solver.core.config.solver.SolverConfig
import io.bluetape4k.spring.tests.httpGet
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import timefold.workshop.school.timetabling.AbstractSchoolTimetablingTest
import timefold.workshop.school.timetabling.domain.Timetable
import java.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimetableEnvironmentTest(
    @Autowired private val client: WebTestClient,
    @Autowired private val solverConfig: SolverConfig,
): AbstractSchoolTimetablingTest() {

    @Test
    fun `solve full assert`() = runTest(timeout = 1.minutes) {
        solve(EnvironmentMode.FULL_ASSERT)
    }

    @Test
    fun `solve step assert`() = runTest(timeout = 1.minutes) {
        solve(EnvironmentMode.STEP_ASSERT)
    }

    private suspend fun solve(environmentMode: EnvironmentMode) {
        // Load the problem
        val problem: Timetable = client.httpGet("/demo-data/SMALL")
            .returnResult<Timetable>().responseBody
            .awaitSingle()

        // Update the environment
        val updatedConfig: SolverConfig = solverConfig.copyConfig()
            .withTerminationSpentLimit(Duration.ofSeconds(30))
            .withEnvironmentMode(environmentMode)

        val solverFactory = SolverFactory.create<Timetable>(updatedConfig)

        // Solve the problem
        val solver = solverFactory.buildSolver()
        val solution = solver.solve(problem)

        solution.score?.isFeasible.shouldNotBeNull().shouldBeTrue()
    }
}
