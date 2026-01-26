package timefold.workshop.persistence.exposed

import io.bluetape4k.exposed.r2dbc.tests.R2dbcExposedTestBase
import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging

abstract class AbstractScoreExposedTest: R2dbcExposedTestBase() {

    companion object: KLogging() {
        @JvmStatic
        protected val faker = Fakers.faker

        protected const val REPEAT_SIZE = 5
    }
}
