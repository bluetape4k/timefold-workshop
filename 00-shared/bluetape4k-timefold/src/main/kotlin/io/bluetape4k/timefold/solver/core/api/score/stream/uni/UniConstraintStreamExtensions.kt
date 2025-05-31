package io.bluetape4k.timefold.solver.core.api.score.stream.uni

import ai.timefold.solver.core.api.score.stream.bi.BiConstraintStream
import ai.timefold.solver.core.api.score.stream.bi.BiJoiner
import ai.timefold.solver.core.api.score.stream.uni.UniConstraintStream


inline fun <A, reified B> UniConstraintStream<A>.join(): BiConstraintStream<A, B> =
    join(B::class.java)

inline fun <A, reified B> UniConstraintStream<A>.join(joiner: BiJoiner<A, B>): BiConstraintStream<A, B> =
    join(B::class.java, joiner)

inline fun <A, reified B> UniConstraintStream<A>.join(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
): BiConstraintStream<A, B> =
    join(B::class.java, joiner1, joiner2)

inline fun <A, reified B> UniConstraintStream<A>.join(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
): BiConstraintStream<A, B> =
    join(B::class.java, joiner1, joiner2, joiner3)

inline fun <A, reified B> UniConstraintStream<A>.join(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
    joiner4: BiJoiner<A, B>,
): BiConstraintStream<A, B> =
    join(B::class.java, joiner1, joiner2, joiner3, joiner4)

inline fun <A, reified B> UniConstraintStream<A>.join(vararg joiners: BiJoiner<A, B>): BiConstraintStream<A, B> =
    join(B::class.java, *joiners)


inline fun <A, reified B> UniConstraintStream<A>.ifExists(): UniConstraintStream<A> =
    ifExists(B::class.java)

inline fun <A, reified B> UniConstraintStream<A>.ifExists(joiner: BiJoiner<A, B>): UniConstraintStream<A> =
    ifExists(B::class.java, joiner)

inline fun <A, reified B> UniConstraintStream<A>.ifExists(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifExists(B::class.java, joiner1, joiner2)

inline fun <A, reified B> UniConstraintStream<A>.ifExists(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifExists(B::class.java, joiner1, joiner2, joiner3)

inline fun <A, reified B> UniConstraintStream<A>.ifExists(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
    joiner4: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifExists(B::class.java, joiner1, joiner2, joiner3, joiner4)

inline fun <A, reified B> UniConstraintStream<A>.ifExists(vararg joiners: BiJoiner<A, B>): UniConstraintStream<A> =
    ifExists(B::class.java, *joiners)


inline fun <A, reified B> UniConstraintStream<A>.ifExistsIncludingUnassigned(): UniConstraintStream<A> =
    ifExistsIncludingUnassigned(B::class.java)

inline fun <A, reified B> UniConstraintStream<A>.ifExistsIncludingUnassigned(joiner: BiJoiner<A, B>): UniConstraintStream<A> =
    ifExistsIncludingUnassigned(B::class.java, joiner)

inline fun <A, reified B> UniConstraintStream<A>.ifExistsIncludingUnassigned(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifExistsIncludingUnassigned(B::class.java, joiner1, joiner2)

inline fun <A, reified B> UniConstraintStream<A>.ifExistsIncludingUnassigned(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifExistsIncludingUnassigned(B::class.java, joiner1, joiner2, joiner3)

inline fun <A, reified B> UniConstraintStream<A>.ifExistsIncludingUnassigned(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
    joiner4: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifExistsIncludingUnassigned(B::class.java, joiner1, joiner2, joiner3, joiner4)

inline fun <A, reified B> UniConstraintStream<A>.ifExistsIncludingUnassigned(vararg joiners: BiJoiner<A, B>): UniConstraintStream<A> =
    ifExistsIncludingUnassigned(B::class.java, *joiners)


inline fun <reified A> UniConstraintStream<A>.ifExistsOther(): UniConstraintStream<A> =
    ifExistsOther(A::class.java)

inline fun <reified A> UniConstraintStream<A>.ifExistsOther(joiner: BiJoiner<A, A>): UniConstraintStream<A> =
    ifExistsOther(A::class.java, joiner)

inline fun <reified A> UniConstraintStream<A>.ifExistsOther(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifExistsOther(A::class.java, joiner1, joiner2)

inline fun <reified A> UniConstraintStream<A>.ifExistsOther(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
    joiner3: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifExistsOther(A::class.java, joiner1, joiner2, joiner3)

inline fun <reified A> UniConstraintStream<A>.ifExistsOther(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
    joiner3: BiJoiner<A, A>,
    joiner4: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifExistsOther(A::class.java, joiner1, joiner2, joiner3, joiner4)


inline fun <reified A> UniConstraintStream<A>.ifExistsOther(vararg joiners: BiJoiner<A, A>): UniConstraintStream<A> =
    ifExistsOther(A::class.java, *joiners)


inline fun <reified A> UniConstraintStream<A>.ifExistsOtherIncludingUnassigned(): UniConstraintStream<A> =
    ifExistsOtherIncludingUnassigned(A::class.java)

inline fun <reified A> UniConstraintStream<A>.ifExistsOtherIncludingUnassigned(joiner: BiJoiner<A, A>): UniConstraintStream<A> =
    ifExistsOtherIncludingUnassigned(A::class.java, joiner)

inline fun <reified A> UniConstraintStream<A>.ifExistsOtherIncludingUnassigned(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifExistsOtherIncludingUnassigned(A::class.java, joiner1, joiner2)

inline fun <reified A> UniConstraintStream<A>.ifExistsOtherIncludingUnassigned(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
    joiner3: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifExistsOtherIncludingUnassigned(A::class.java, joiner1, joiner2, joiner3)

inline fun <reified A> UniConstraintStream<A>.ifExistsOtherIncludingUnassigned(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
    joiner3: BiJoiner<A, A>,
    joiner4: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifExistsOtherIncludingUnassigned(A::class.java, joiner1, joiner2, joiner3, joiner4)


inline fun <reified A> UniConstraintStream<A>.ifExistsOtherIncludingUnassigned(vararg joiners: BiJoiner<A, A>): UniConstraintStream<A> =
    ifExistsOtherIncludingUnassigned(A::class.java, *joiners)


inline fun <A, reified B> UniConstraintStream<A>.ifNotExists(): UniConstraintStream<A> =
    ifNotExists(B::class.java)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExists(joiner: BiJoiner<A, B>): UniConstraintStream<A> =
    ifNotExists(B::class.java, joiner)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExists(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifNotExists(B::class.java, joiner1, joiner2)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExists(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifNotExists(B::class.java, joiner1, joiner2, joiner3)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExists(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
    joiner4: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifNotExists(B::class.java, joiner1, joiner2, joiner3, joiner4)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExists(vararg joiners: BiJoiner<A, B>): UniConstraintStream<A> =
    ifNotExists(B::class.java, *joiners)


inline fun <A, reified B> UniConstraintStream<A>.ifNotExistsIncludingUnassigned(): UniConstraintStream<A> =
    ifNotExistsIncludingUnassigned(B::class.java)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExistsIncludingUnassigned(joiner: BiJoiner<A, B>): UniConstraintStream<A> =
    ifNotExistsIncludingUnassigned(B::class.java, joiner)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExistsIncludingUnassigned(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifNotExistsIncludingUnassigned(B::class.java, joiner1, joiner2)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExistsIncludingUnassigned(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifNotExistsIncludingUnassigned(B::class.java, joiner1, joiner2, joiner3)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExistsIncludingUnassigned(
    joiner1: BiJoiner<A, B>,
    joiner2: BiJoiner<A, B>,
    joiner3: BiJoiner<A, B>,
    joiner4: BiJoiner<A, B>,
): UniConstraintStream<A> =
    ifNotExistsIncludingUnassigned(B::class.java, joiner1, joiner2, joiner3, joiner4)

inline fun <A, reified B> UniConstraintStream<A>.ifNotExistsIncludingUnassigned(vararg joiners: BiJoiner<A, B>): UniConstraintStream<A> =
    ifNotExistsIncludingUnassigned(B::class.java, *joiners)


inline fun <reified A> UniConstraintStream<A>.ifNotExistsOtherIncludingUnassigned(): UniConstraintStream<A> =
    ifNotExistsOtherIncludingUnassigned(A::class.java)

inline fun <reified A> UniConstraintStream<A>.ifNotExistsOtherIncludingUnassigned(joiner: BiJoiner<A, A>): UniConstraintStream<A> =
    ifNotExistsOtherIncludingUnassigned(A::class.java, joiner)

inline fun <reified A> UniConstraintStream<A>.ifNotExistsOtherIncludingUnassigned(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifNotExistsOtherIncludingUnassigned(A::class.java, joiner1, joiner2)

inline fun <reified A> UniConstraintStream<A>.ifNotExistsOtherIncludingUnassigned(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
    joiner3: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifNotExistsOtherIncludingUnassigned(A::class.java, joiner1, joiner2, joiner3)

inline fun <reified A> UniConstraintStream<A>.ifNotExistsOtherIncludingUnassigned(
    joiner1: BiJoiner<A, A>,
    joiner2: BiJoiner<A, A>,
    joiner3: BiJoiner<A, A>,
    joiner4: BiJoiner<A, A>,
): UniConstraintStream<A> =
    ifNotExistsOtherIncludingUnassigned(A::class.java, joiner1, joiner2, joiner3, joiner4)


inline fun <reified A> UniConstraintStream<A>.ifNotExistsOtherIncludingUnassigned(vararg joiners: BiJoiner<A, A>): UniConstraintStream<A> =
    ifNotExistsOtherIncludingUnassigned(A::class.java, *joiners)
