package timefold.workshop.school.timetabling

import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [SchoolTimetablingApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
abstract class AbstractSchoolTimetablingTest {

    companion object: KLoggingChannel()

}
