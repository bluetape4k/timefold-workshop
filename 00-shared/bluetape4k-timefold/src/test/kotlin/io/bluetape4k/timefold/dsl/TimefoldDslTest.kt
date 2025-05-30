package io.bluetape4k.timefold.dsl

import io.bluetape4k.logging.coroutines.KLoggingChannel
import io.bluetape4k.timefold.AbstractTimefoldTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.Test

class TimefoldDslTest: AbstractTimefoldTest() {

    companion object: KLoggingChannel()

    data class Task(
        val id: Long,
        val name: String,
        var assignedTo: String? = null,
    )

    data class Employee(val name: String)

    @Test
    fun `planning entity 와 constraints 를 빌드합니다`() {
        val entity = planningEntity<Task> {
            planningVariable(Task::assignedTo)
        }
        val constraints = constraintSet {
            constraint("💫No duplicate assignment 🙌") {
                println("\uD83D\uDD27 Hard Constraint evaluated.")
            }
            constraint("🍦Balance workload", isHard = false) {
                println("✨ Soft Constraint evaluated")
            }
        }

        entity.kclazz shouldBeEqualTo Task::class
        entity.variables shouldHaveSize 1

        constraints shouldHaveSize 2
        constraints[0].name shouldBeEqualTo "💫No duplicate assignment 🙌"
        constraints[1].name shouldBeEqualTo "🍦Balance workload"

        val factory = solverFactory<Task>(constraints).build()
    }

    @Test
    fun `build solver factory`() {
        val constraints = constraintSet {
            hardConstraint("Hard rule")
            softConstraint("Soft rule")
        }

        val factory = solverFactory<Task>(constraints).build()
        factory shouldContain "MockSolverFactoryInstance"
    }

//    fun main() {
//        // 1. Entity 정의
//        val taskEntity: PlanningEntityDefinition<Task> = planningEntity<Task> {
//            planningVariable(Task::assignedTo)
//        }
//
//        // 2. Constraint 정의
//        val constraints: List<ConstraintDefinition> = constraintSet {
//            constraint("No duplicate assignment") {
//                println("\uD83D\uDD27  Hard Constraint evaluated")
//            }
//            constraint("Balance workload", isHard =  false) {
//                println("✨ Soft Constraint evaluated")
//            }
//        }
//
//        // 3. Solution 정의
//        val solution = planningSolution {
//            add(Task(1, "Implement DSL"))
//            add(Task(2, "Write tests"))
//            score("0hard/0soft")
//        }
//
//        // 4. 전체 모델 작성
//        timefoldModel(
//            entities = listOf(taskEntity),
//            constraints = constraints,
//            solution = solution
//        )
//    }
}
