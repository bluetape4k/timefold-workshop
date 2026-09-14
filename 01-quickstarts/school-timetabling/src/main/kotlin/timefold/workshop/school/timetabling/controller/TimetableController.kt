package timefold.workshop.school.timetabling.controller

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis
import ai.timefold.solver.core.api.score.HardSoftScore
import ai.timefold.solver.core.api.solver.ScoreAnalysisFetchPolicy
import ai.timefold.solver.core.api.solver.SolutionManager
import ai.timefold.solver.core.api.solver.SolverManager
import io.bluetape4k.logging.coroutines.KLoggingChannel
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.error
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import timefold.workshop.school.timetabling.domain.Timetable
import timefold.workshop.school.timetabling.exceptions.TimetableSolverException
import timefold.workshop.school.timetabling.solver.justifications.RoomConflictJustification
import timefold.workshop.school.timetabling.solver.justifications.StudentGroupConflictJustification
import timefold.workshop.school.timetabling.solver.justifications.StudentGroupSubjectVarietyJustification
import timefold.workshop.school.timetabling.solver.justifications.TeacherConflictJustification
import timefold.workshop.school.timetabling.solver.justifications.TeacherRoomStabilityJustification
import timefold.workshop.school.timetabling.solver.justifications.TeacherTimeEfficiencyJustification
import java.util.UUID

@RestController
@RequestMapping("/timetables")
class TimetableController(
    private val solverManager: SolverManager<Timetable>,
    private val solutionManager: SolutionManager<Timetable, HardSoftScore>,
    private val jobRegistry: TimetableJobRegistry,
): CoroutineScope by CoroutineScope(Dispatchers.IO + CoroutineName("timetable")) {

    companion object: KLoggingChannel()

    @GetMapping
    suspend fun getJobIds(): Collection<String> = jobRegistry.jobIds()

    @PostMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun solve(@RequestBody problem: Timetable): String {
        log.debug { "Received timetable solve request" }

        val jobId = UUID.randomUUID().toString()
        jobRegistry.start(jobId, problem)

        log.debug { "Starting solver for jobId: $jobId" }
        try {
            solverManager
                .solveBuilder()
                .withProblemId(jobId)
                .withProblemFinder { id -> jobRegistry.get(id.toString()).timetable }
                .withBestSolutionEventConsumer { event ->
                    event.solution()?.let { solution -> jobRegistry.recordBest(jobId, solution) }
                }
                .withFinalBestSolutionEventConsumer { event ->
                    event.solution()?.let { solution ->
                        jobRegistry.recordFinal(jobId, solution)
                    } ?: jobRegistry.recordFailure(
                        jobId,
                        IllegalStateException("Solver completed without a final solution"),
                    )
                }
                .withExceptionHandler { id, exception ->
                    val callbackJobId = id.toString()
                    jobRegistry.recordFailure(callbackJobId, exception)
                    log.error(exception) { "Solver failed for jobId: $callbackJobId" }
                }
                .run()
        } catch (exception: Exception) {
            jobRegistry.recordFailure(jobId, exception)
            throw exception
        }

        return jobId
    }

    @PutMapping(value = ["/analyze"])
    @RegisterReflectionForBinding(
        RoomConflictJustification::class,
        StudentGroupConflictJustification::class,
        StudentGroupSubjectVarietyJustification::class,
        TeacherConflictJustification::class,
        TeacherRoomStabilityJustification::class,
        TeacherTimeEfficiencyJustification::class
    )
    suspend fun analyze(
        @RequestBody problem: Timetable,
        @RequestParam("fetchPolicy", required = false) fetchPolicy: ScoreAnalysisFetchPolicy? = null,
    ): ScoreAnalysis<HardSoftScore> {
        return fetchPolicy?.let { solutionManager.analyze(problem, it) }
            ?: solutionManager.analyze(problem)
    }


    @GetMapping("/{jobId}")
    suspend fun getTimetable(
        @PathVariable("jobId") jobId: String,
    ): Timetable {
        val timetable = getTimetableAndCheckForExceptions(jobId)
        val solverStatus = solverManager.getSolverStatus(jobId)
        return timetable.copy(solverStatus = solverStatus)
    }

    @GetMapping("/{jobId}/status")
    suspend fun getStatus(
        @PathVariable("jobId") jobId: String,
    ): Timetable {
        val timetable = getTimetableAndCheckForExceptions(jobId)
        val solverStatus = solverManager.getSolverStatus(jobId)
        return Timetable(timetable.name, timetable.score, solverStatus)
    }

    private fun getTimetableAndCheckForExceptions(jobId: String): Timetable {
        val snapshot = jobRegistry.get(jobId)
        if (snapshot.state == CompletionState.FAILED) {
            throw TimetableSolverException(
                jobId,
                snapshot.exception ?: IllegalStateException("Solver failed without an exception"),
            )
        }
        return snapshot.timetable
    }

    @DeleteMapping("/{jobId}")
    suspend fun terminateSolving(
        @PathVariable("jobId") jobId: String,
    ): Timetable {
        log.debug { "Terminating solver for jobId: $jobId" }
        solverManager.terminateEarly(jobId)

        return getTimetable(jobId)
    }
}
