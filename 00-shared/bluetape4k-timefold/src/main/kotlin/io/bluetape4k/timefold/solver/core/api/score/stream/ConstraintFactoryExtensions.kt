package io.bluetape4k.timefold.solver.core.api.score.stream

import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.bi.BiConstraintStream
import ai.timefold.solver.core.api.score.stream.bi.BiJoiner
import ai.timefold.solver.core.api.score.stream.uni.UniConstraintStream

inline fun <reified T: Any> ConstraintFactory.forEach(): UniConstraintStream<T> =
    forEach(T::class.java)

inline fun <reified T: Any> ConstraintFactory.forEachIncludingUnassigned(): UniConstraintStream<T> =
    forEachIncludingUnassigned(T::class.java)

inline fun <reified T: Any> ConstraintFactory.forEachUniquePair(): BiConstraintStream<T, T> =
    forEachUniquePair(T::class.java)

inline fun <reified T: Any> ConstraintFactory.forEachUniquePair(
    joiner: BiJoiner<T, T>,
): BiConstraintStream<T, T> =
    forEachUniquePair(T::class.java, joiner)

inline fun <reified T: Any> ConstraintFactory.forEachUniquePair(
    joiner1: BiJoiner<T, T>,
    joiner2: BiJoiner<T, T>,
): BiConstraintStream<T, T> =
    forEachUniquePair(T::class.java, joiner1, joiner2)

inline fun <reified T: Any> ConstraintFactory.forEachUniquePair(
    joiner1: BiJoiner<T, T>,
    joiner2: BiJoiner<T, T>,
    joiner3: BiJoiner<T, T>,
): BiConstraintStream<T, T> =
    forEachUniquePair(T::class.java, joiner1, joiner2, joiner3)

inline fun <reified T: Any> ConstraintFactory.forEachUniquePair(
    joiner1: BiJoiner<T, T>,
    joiner2: BiJoiner<T, T>,
    joiner3: BiJoiner<T, T>,
    joiner4: BiJoiner<T, T>,
): BiConstraintStream<T, T> =
    forEachUniquePair(T::class.java, joiner1, joiner2, joiner3, joiner4)

inline fun <reified T: Any> ConstraintFactory.forEachUniquePair(
    vararg joiners: BiJoiner<T, T>,
): BiConstraintStream<T, T> =
    forEachUniquePair(T::class.java, *joiners)
