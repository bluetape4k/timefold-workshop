package io.bluetape4k.timefold.dsl

data class ConstraintDefinition(
    val name: String,
    val isHard: Boolean,
    val block: () -> Unit = {},  // 실제 ContraintFactory를 이용한 정의는 별도 모듈에서 처리
)

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

inline fun constraintSet(block: ConstraintBuilderDsl.() -> Unit): List<ConstraintDefinition> =
    ConstraintBuilderDsl().apply(block).build()
