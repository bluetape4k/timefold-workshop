package timefold.workshop.bed.allocation

import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BedAllocationApplication {

    companion object: KLoggingChannel()

}

fun main(args: Array<String>) {
    runApplication<BedAllocationApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}
