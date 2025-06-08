package timefold.workshop.bed.allocation

import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
abstract class AbstractBedAllocationApplicationTest {

    companion object: KLoggingChannel()

}
