package timefold.workshop.school.timetabling.controller

import ai.timefold.solver.core.api.score.analysis.ScoreAnalysis
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
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
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/timetables")
class TimetableController(
    private val solverManager: SolverManager<Timetable, String>,
    private val solutionManager: SolutionManager<Timetable, HardSoftScore>,
): CoroutineScope by CoroutineScope(Dispatchers.IO + CoroutineName("timetable")) {

    companion object: KLoggingChannel()

    // TODO: Without any "time to live", the map may eventually grow out of memory.
    private val jobIdToJob = ConcurrentHashMap<String, Job>()

    private data class Job(val timetable: Timetable?, val exception: Throwable? = null) {
        companion object {
            fun ofTimetable(timetable: Timetable): Job = Job(timetable)
            fun ofException(error: Throwable): Job = Job(null, error)
        }
    }

    @GetMapping
    suspend fun getJobIds(): Collection<String> = jobIdToJob.keys

    @PostMapping(produces = [MediaType.TEXT_PLAIN_VALUE])
    suspend fun solve(@RequestBody problem: Timetable): String {
        log.debug { "Received problem: $problem" }

        val jobId = UUID.randomUUID().toString()
        jobIdToJob[jobId] = Job.ofTimetable(problem)

        log.debug { "Starting solver for jobId: $jobId" }
        solverManager
            .solveBuilder()
            .withProblemId(jobId)
            .withProblemFinder { jobIdToJob[it]!!.timetable }
            .withBestSolutionConsumer { solution ->
                jobIdToJob[jobId] = Job.ofTimetable(solution)
            }
            .withExceptionHandler { jobId, exception ->
                jobIdToJob[jobId] = Job.ofException(exception)
                log.error(exception) { "Solver failed for jobId: $jobId" }
            }
            .run()

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
        timetable.solverStatus = solverStatus
        return timetable
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
        val job = jobIdToJob[jobId]
            ?: throw TimetableSolverException(jobId, HttpStatus.NOT_FOUND, "No timetable found")

        if (job.exception != null) {
            throw TimetableSolverException(jobId, job.exception)
        }
        return job.timetable!!
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
