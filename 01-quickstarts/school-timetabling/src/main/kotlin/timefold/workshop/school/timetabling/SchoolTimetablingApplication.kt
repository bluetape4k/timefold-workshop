package timefold.workshop.school.timetabling

import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SchoolTimetablingApplication {

    companion object: KLoggingChannel()

}

fun main(args: Array<String>) {
    runApplication<SchoolTimetablingApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
