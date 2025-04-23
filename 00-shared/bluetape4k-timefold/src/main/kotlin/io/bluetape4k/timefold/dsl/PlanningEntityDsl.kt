package io.bluetape4k.timefold.dsl

import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * 프래닝 엔티티 정의 정보를 DSL에서 사용하기 위한 클래스입니다.
 *
 * 플래닝 엔티티는 플래닝 문제에서 스케줄링 또는 할당되는 객체입니다.
 *
 * @param T 플래닝 엔티티의 타입
 * @property clazz 플래닝 엔티티의 클래스
 * @property variables 플래닝 엔티티의 변수 리스트입니다.
 */
data class PlanningEntityDefinition<T: Any>(
    val clazz: KClass<T>,
    val variables: List<KProperty1<T, *>>,
): Serializable

/**
 * 플래닝 엔티티를 구성하기 위한 DSL입니다.
 *
 * @param T 플래닝 엔티티의 타입
 * @property clazz 플래닝 엔티티의 클래스
 */
class PlanningEntityBuilder<T: Any>(val clazz: KClass<T>) {
    private val variables = mutableListOf<KProperty1<T, *>>()

    fun planningVariable(prop: KProperty1<T, *>) {
        variables.add(prop)
    }

    fun build(): PlanningEntityDefinition<T> =
        PlanningEntityDefinition(clazz, variables)
}

/**
 * DSL function to create a [PlanningEntityDefinition] for a given class.
 * 주어진 클래스에 대해 [PlanningEntityDefinition]을 생성하기 위한 DSL 함수입니다.
 *
 * @param T 플래닝 엔티티의 타입
 * @param block 플래닝 엔티티를 정의하는 블록입니다.
 * @return a 지정한 클래스를 위한 [PlanningEntityDefinition] 인스턴스입니다.
 */
inline fun <reified T: Any> planningEntity(
    block: PlanningEntityBuilder<T>.() -> Unit,
): PlanningEntityDefinition<T> =
    PlanningEntityBuilder(T::class).apply(block).build()
