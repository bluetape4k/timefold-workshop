package timefold.workshop.school.timetabling.exceptions

import org.springframework.http.HttpStatus

class TimetableSolverException: RuntimeException {

    val jobId: String
    val status: HttpStatus

    constructor(jobId: String, status: HttpStatus, message: String): super(message) {
        this.jobId = jobId
        this.status = status
    }

    constructor(jobId: String, cause: Throwable): super(cause.message, cause) {
        this.jobId = jobId
        this.status = HttpStatus.INTERNAL_SERVER_ERROR
    }
}
