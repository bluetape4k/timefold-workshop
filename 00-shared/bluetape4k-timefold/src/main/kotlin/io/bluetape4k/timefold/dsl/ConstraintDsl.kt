package io.bluetape4k.timefold.dsl

import java.io.Serializable

/**
 * 제약 조건들을 정의하는 DSL입니다.
 *
 * @param block 제약 조건을 정의하는 블록입니다.
 */
data class ConstraintDefinition(
    val name: String,
    val isHard: Boolean,
    val block: () -> Unit = {},  // 실제 ContraintFactory를 이용한 정의는 별도 모듈에서 처리
): Serializable

/**
 * 제약 조건들을 정의하는 DSL입니다.
 *
 * @property constraints  [ConstraintDefinition] 리스트입니다.
 */
class ConstraintBuilderDsl {
    private val constraints = mutableListOf<ConstraintDefinition>()

    fun constraint(name: String, isHard: Boolean = true, block: () -> Unit = {}) {
        constraints += ConstraintDefinition(name, isHard, block)
    }

    fun hardConstraint(name: String, block: () -> Unit = {}) {
        constraints += ConstraintDefinition(name, isHard = true, block)
    }

    fun softConstraint(name: String, block: () -> Unit = {}) {
        constraints += ConstraintDefinition(name, isHard = false, block)
    }

    fun build(): List<ConstraintDefinition> = constraints
}

/**
 * 제약 조건을 정의하는 DSL 함수입니다.
 *
 * @param block 제약 조건을 정의하는 블록입니다.
 * @return 제약 조건 정의([ConstraintDefinition]) 리스트입니다.
 */
inline fun constraintSet(block: ConstraintBuilderDsl.() -> Unit): List<ConstraintDefinition> =
    ConstraintBuilderDsl().apply(block).build()
