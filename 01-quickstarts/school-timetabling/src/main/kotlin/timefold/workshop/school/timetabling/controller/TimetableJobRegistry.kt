package timefold.workshop.school.timetabling.controller

import io.bluetape4k.support.requireNotBlank
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import timefold.workshop.school.timetabling.domain.Timetable
import timefold.workshop.school.timetabling.exceptions.TimetableSolverException

/**
 * SolverManager callback 결과를 한 번의 terminal 전이로 보관합니다.
 *
 * 중간 best 결과는 [CompletionState.PENDING]에서만 교체하고, final 또는
 * exception callback이 먼저 도착하면 이후 callback은 무시합니다. entry별
 * lock으로 callback과 조회가 서로의 부분 상태를 관찰하지 않도록 합니다.
 */
@Component
class TimetableJobRegistry {

    private class Entry(
        var timetable: Timetable,
        var exception: Throwable? = null,
        var state: CompletionState = CompletionState.PENDING,
        val lock: ReentrantLock = ReentrantLock(),
    )

    // TODO: delete/expire completed entries after an explicit API retention contract is defined.
    private val entries = ConcurrentHashMap<String, Entry>()

    /** 새 solver job을 pending 상태로 등록합니다. */
    internal fun start(jobId: String, problem: Timetable) {
        jobId.requireNotBlank("jobId")
        check(entries.putIfAbsent(jobId, Entry(problem)) == null) {
            "Duplicate solver jobId: $jobId"
        }
    }

    /** 아직 완료되지 않은 job의 중간 best 결과만 갱신합니다. */
    internal fun recordBest(jobId: String, solution: Timetable) {
        entries[jobId]?.let { entry ->
            entry.lock.withLock {
                if (entry.state == CompletionState.PENDING) {
                    entry.timetable = solution
                }
            }
        }
    }

    /** 정상 완료를 terminal 상태로 기록하고 이후 callback을 차단합니다. */
    internal fun recordFinal(jobId: String, solution: Timetable) {
        entries[jobId]?.let { entry ->
            entry.lock.withLock {
                if (entry.state == CompletionState.PENDING) {
                    entry.timetable = solution
                    entry.state = CompletionState.COMPLETED
                }
            }
        }
    }

    /** 예외 완료를 terminal 상태로 기록하고 이후 callback을 차단합니다. */
    internal fun recordFailure(jobId: String, error: Throwable) {
        entries[jobId]?.let { entry ->
            entry.lock.withLock {
                if (entry.state == CompletionState.PENDING) {
                    entry.exception = error
                    entry.state = CompletionState.FAILED
                }
            }
        }
    }

    /** job의 immutable snapshot을 반환하고, 모르는 ID는 HTTP 404로 변환합니다. */
    internal fun get(jobId: String): EntrySnapshot = entries[jobId]?.let { entry ->
        entry.lock.withLock {
            EntrySnapshot(
                timetable = entry.timetable,
                exception = entry.exception,
                state = entry.state,
            )
        }
    } ?: throw TimetableSolverException(jobId, HttpStatus.NOT_FOUND, "No timetable found")

    /** 현재 등록된 job ID를 live map과 분리된 snapshot으로 반환합니다. */
    internal fun jobIds(): Set<String> = entries.keys.toSet()
}

/** Solver job의 완료 전이 상태입니다. */
internal enum class CompletionState {
    PENDING,
    COMPLETED,
    FAILED,
}

/** 외부 caller가 mutable registry entry를 직접 변경하지 못하도록 만든 snapshot입니다. */
internal data class EntrySnapshot(
    val timetable: Timetable,
    val exception: Throwable?,
    val state: CompletionState,
)
