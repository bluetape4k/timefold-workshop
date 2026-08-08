package io.bluetape4k.timefold.dsl

import kotlin.reflect.KClass

/**
 * 솔버 팩토리를 정의하기 위한 DSL입니다.
 *
 * @param T 플래닝 솔루션의 타입입니다.
 * @property planningSolutionClazz 플래닝 솔루션의 클래스입니다.
 * @property constraints 제약 조건 정의 목록입니다.
 */
class SolverFactoryDsl<T: Any>(
    val planningSolutionClazz: KClass<T>,
    val constraints: List<ConstraintDefinition>,
) {
    fun build(): String {
        println("🧩 SolverFactory created for: ${planningSolutionClazz.simpleName}")
        constraints.forEach {
            println("→ constraint: ${it.name} (${if (it.isHard) "HARD" else "SOFT"})")
        }
        return "MockSolverFactoryInstance"
    }
}

/**
 * 주어진 클래스에 대한 [SolverFactoryDsl]을 생성하는 DSL 함수입니다.
 *
 * @param T 플래닝 솔루션의 타입입니다.
 * @param constraints 제약 조건 정의 목록입니다.
 * @return 지정한 클래스에 대한 [SolverFactoryDsl] 인스턴스입니다.
 */
inline fun <reified T: Any> solverFactory(
    constraints: List<ConstraintDefinition>,
): SolverFactoryDsl<T> =
    SolverFactoryDsl(T::class, constraints)
