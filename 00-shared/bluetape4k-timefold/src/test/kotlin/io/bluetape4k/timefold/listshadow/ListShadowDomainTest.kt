package io.bluetape4k.timefold.listshadow

import ai.timefold.solver.core.api.score.SimpleScore
import ai.timefold.solver.core.api.score.calculator.EasyScoreCalculator
import ai.timefold.solver.core.api.solver.SolutionManager
import ai.timefold.solver.core.api.solver.SolverFactory
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig
import ai.timefold.solver.core.config.solver.EnvironmentMode
import ai.timefold.solver.core.config.solver.SolverConfig
import ai.timefold.solver.core.config.solver.termination.TerminationConfig
import io.bluetape4k.assertions.shouldBeEqualTo
import io.bluetape4k.assertions.shouldBeNull
import io.bluetape4k.assertions.shouldNotBeNull
import org.junit.jupiter.api.Test

class ListShadowDomainTest {

    @Test
    fun `index and declarative position refresh after reorder`() {
        val a = ListShadowVisit("a", "A")
        val b = ListShadowVisit("b", "B")
        val c = ListShadowVisit("c", "C")
        val route = ListShadowRoute("r1", mutableListOf(a, b, c))
        val solution = ListShadowPlan(listOf(route), listOf(a, b, c))

        SolutionManager.updateShadowVariables(solution)
        route.visits.map { it.indexInRoute } shouldBeEqualTo listOf(0, 1, 2)
        route.visits.map { it.sequencePosition } shouldBeEqualTo listOf(1, 2, 3)

        route.visits = mutableListOf(c, a, b)
        SolutionManager.updateShadowVariables(solution)

        route.visits.map { it.indexInRoute } shouldBeEqualTo listOf(0, 1, 2)
        route.visits.map { it.sequencePosition } shouldBeEqualTo listOf(1, 2, 3)
        c.sequencePosition shouldBeEqualTo 1
        a.sequencePosition shouldBeEqualTo 2
        b.sequencePosition shouldBeEqualTo 3
    }

    @Test
    fun `moving a visit between routes and unassigning refreshes nullable shadows`() {
        val a = ListShadowVisit("a", "A")
        val b = ListShadowVisit("b", "B")
        val firstRoute = ListShadowRoute("r1", mutableListOf(a, b))
        val secondRoute = ListShadowRoute("r2")
        val solution = ListShadowPlan(listOf(firstRoute, secondRoute), listOf(a, b))

        SolutionManager.updateShadowVariables(solution)
        b.indexInRoute shouldBeEqualTo 1
        b.sequencePosition shouldBeEqualTo 2

        firstRoute.visits.remove(b)
        secondRoute.visits.add(b)
        SolutionManager.updateShadowVariables(solution)
        b.indexInRoute shouldBeEqualTo 0
        b.sequencePosition shouldBeEqualTo 1

        secondRoute.visits.remove(b)
        SolutionManager.updateShadowVariables(solution)
        b.indexInRoute.shouldBeNull()
        b.sequencePosition.shouldBeNull()
    }

    @Test
    fun `full assert solver registers both planning entity types`() {
        val a = ListShadowVisit("a", "A")
        val b = ListShadowVisit("b", "B")
        val c = ListShadowVisit("c", "C")
        val route = ListShadowRoute("r1", mutableListOf(a, b, c))
        val solution = ListShadowPlan(listOf(route), listOf(a, b, c))
        val solverConfig = SolverConfig()
            .withSolutionClass(ListShadowPlan::class.java)
            .withEntityClasses(ListShadowRoute::class.java, ListShadowVisit::class.java)
            .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
            .withTerminationConfig(TerminationConfig().withStepCountLimit(1))
            .withScoreDirectorFactory(
                ScoreDirectorFactoryConfig()
                    .withEasyScoreCalculatorClass(ListShadowZeroScoreCalculator::class.java),
            )

        val solved = SolverFactory.create<ListShadowPlan>(solverConfig).buildSolver().solve(solution)

        solved.routes.single().visits.map { it.sequencePosition } shouldBeEqualTo listOf(1, 2, 3)
        solved.score.shouldNotBeNull()
    }

}

class ListShadowZeroScoreCalculator : EasyScoreCalculator<ListShadowPlan, SimpleScore> {
    override fun calculateScore(solution: ListShadowPlan): SimpleScore = SimpleScore.ZERO
}
