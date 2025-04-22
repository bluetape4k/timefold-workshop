package io.bluetape4k.timefold

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging

abstract class AbstractTimefoldTest {

    companion object: KLogging() {
        @JvmStatic
        val faker = Fakers.faker
    }
}
