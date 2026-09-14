package timefold.workshop.school.timetabling.controller

import io.bluetape4k.assertions.assertFailsWith
import io.bluetape4k.assertions.shouldBeEqualTo
import io.bluetape4k.assertions.shouldBeNull
import io.bluetape4k.assertions.shouldBeTrue
import io.bluetape4k.assertions.shouldNotBeNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import timefold.workshop.school.timetabling.domain.Timetable
import timefold.workshop.school.timetabling.exceptions.TimetableSolverException

class TimetableJobRegistryTest {

    private lateinit var registry: TimetableJobRegistry

    @BeforeEach
    fun setUp() {
        registry = TimetableJobRegistry()
    }

    @Test
    fun `best then final keeps the final solution`() {
        registry.start("job-1", Timetable(name = "initial"))

        registry.recordBest("job-1", Timetable(name = "best"))
        registry.recordFinal("job-1", Timetable(name = "final"))
        registry.recordFinal("job-1", Timetable(name = "late-final"))
        registry.recordBest("job-1", Timetable(name = "late-best"))

        val snapshot = registry.get("job-1")
        snapshot.state shouldBeEqualTo CompletionState.COMPLETED
        snapshot.timetable.name shouldBeEqualTo "final"
        snapshot.exception.shouldBeNull()
    }

    @Test
    fun `best then failure keeps the best solution and failure`() {
        registry.start("job-1", Timetable(name = "initial"))
        registry.recordBest("job-1", Timetable(name = "best"))

        val failure = IllegalStateException("solver failed")
        registry.recordFailure("job-1", failure)
        registry.recordBest("job-1", Timetable(name = "late-best"))
        registry.recordFinal("job-1", Timetable(name = "late-final"))

        val snapshot = registry.get("job-1")
        snapshot.state shouldBeEqualTo CompletionState.FAILED
        snapshot.timetable.name shouldBeEqualTo "best"
        snapshot.exception.shouldNotBeNull().message shouldBeEqualTo "solver failed"
    }

    @Test
    fun `duplicate start keeps the original problem`() {
        registry.start("job-1", Timetable(name = "initial"))

        assertFailsWith<IllegalStateException> {
            registry.start("job-1", Timetable(name = "duplicate"))
        }

        registry.get("job-1").timetable.name shouldBeEqualTo "initial"
        registry.jobIds() shouldBeEqualTo setOf("job-1")
    }

    @Test
    fun `blank job id is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            registry.start(" ", Timetable(name = "initial"))
        }
    }

    @Test
    fun `unknown callbacks are ignored and lookup remains not found`() {
        registry.recordBest("missing", Timetable(name = "best"))
        registry.recordFinal("missing", Timetable(name = "final"))
        registry.recordFailure("missing", IllegalStateException("missing"))

        assertFailsWith<TimetableSolverException> {
            registry.get("missing")
        }
    }

    @Test
    fun `concurrent final and failure produce one terminal state`() {
        registry.start("job-1", Timetable(name = "initial"))

        val ready = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)
        try {
            val finalFuture = executor.submit {
                ready.await()
                registry.recordFinal("job-1", Timetable(name = "final"))
            }
            val failureFuture = executor.submit {
                ready.await()
                registry.recordFailure("job-1", IllegalStateException("failure"))
            }

            ready.countDown()
            finalFuture.get(5, TimeUnit.SECONDS)
            failureFuture.get(5, TimeUnit.SECONDS)
        } finally {
            executor.shutdownNow()
        }

        val snapshot = registry.get("job-1")
        (snapshot.state == CompletionState.COMPLETED || snapshot.state == CompletionState.FAILED).shouldBeTrue()
        when (snapshot.state) {
            CompletionState.COMPLETED -> {
                snapshot.timetable.name shouldBeEqualTo "final"
                snapshot.exception.shouldBeNull()
            }

            CompletionState.FAILED -> {
                snapshot.timetable.name shouldBeEqualTo "initial"
                snapshot.exception.shouldNotBeNull().message shouldBeEqualTo "failure"
            }

            CompletionState.PENDING -> error("terminal callback did not settle the job")
        }
    }
}
