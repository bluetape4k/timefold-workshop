package timefold.workshop.persistence.exposed

import ai.timefold.solver.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore
import io.bluetape4k.exposed.r2dbc.tests.TestDB
import io.bluetape4k.exposed.r2dbc.tests.withTables
import io.bluetape4k.junit5.coroutines.runSuspendIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.timefold.solver.exposed.api.score.buildin.hardMediumSoftLongScore
import kotlinx.coroutines.flow.single
import org.amshove.kluent.shouldBeEqualTo
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class HardMediumSoftLongScoreTest: AbstractScoreExposedTest() {

    companion object: KLogging()

    object T1: IntIdTable() {
        val name = varchar("name", 255)
        val hardMediumSoftLongScore = hardMediumSoftLongScore("hardmediumsoft_long_score")
    }

    @ParameterizedTest
    @MethodSource(ENABLE_DIALECTS_METHOD)
    fun `HardMediumSoftLongScore 를 DB 에 저장 및 조회하기`(testDB: TestDB) = runSuspendIO {
        withTables(testDB, T1) {

            val name = faker.name().name()
            val hardMediumSoftLongScore = HardMediumSoftLongScore.of(
                faker.random().nextLong(),
                faker.random().nextLong(),
                faker.random().nextLong()
            )

            val id = T1.insertAndGetId {
                it[T1.name] = name
                it[T1.hardMediumSoftLongScore] = hardMediumSoftLongScore
            }

            val row = T1
                .selectAll()
                .where { T1.id eq id }
                .single()

            log.debug { "row=$row" }

            row[T1.name] shouldBeEqualTo name
            row[T1.hardMediumSoftLongScore] shouldBeEqualTo hardMediumSoftLongScore
        }
    }
}
