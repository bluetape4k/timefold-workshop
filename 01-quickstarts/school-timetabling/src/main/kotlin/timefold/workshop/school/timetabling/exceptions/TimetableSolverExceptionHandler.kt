package timefold.workshop.school.timetabling.exceptions

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class TimetableSolverExceptionHandler {

    @ExceptionHandler(TimetableSolverException::class)
    fun handleTimetableSolverException(ex: TimetableSolverException): ResponseEntity<ErrorInfo> {
        return ResponseEntity
            .status(ex.status)
            .body(ErrorInfo(ex.jobId, ex.message ?: ""))
    }
}
