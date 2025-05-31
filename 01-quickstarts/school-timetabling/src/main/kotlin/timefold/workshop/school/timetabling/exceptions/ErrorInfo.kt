package timefold.workshop.school.timetabling.exceptions

import java.io.Serializable

data class ErrorInfo(
    val jobId: String,
    val message: String,
): Serializable
