package timefold.workshop.school.timetabling.config

import ai.timefold.solver.test.api.score.stream.ConstraintVerifier
import io.bluetape4k.logging.coroutines.KLoggingChannel
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import timefold.workshop.school.timetabling.domain.Lesson
import timefold.workshop.school.timetabling.domain.Timetable
import timefold.workshop.school.timetabling.solver.TimetableConstraintProvider

@TestConfiguration
class ConstraintConfig {

    companion object: KLoggingChannel()

    @Bean
    fun constraintVerifier(): ConstraintVerifier<TimetableConstraintProvider, Timetable> {
        return ConstraintVerifier.build(
            TimetableConstraintProvider(),
            Timetable::class.java,
            Lesson::class.java
        )
    }
}
