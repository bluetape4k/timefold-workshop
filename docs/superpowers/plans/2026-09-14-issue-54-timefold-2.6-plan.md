# Issue #54 Timefold 2.6.0 검증 Implementation Plan

> **For agentic workers:** 이 계획은 test-driven-development와
> bluetape-kotlin-patterns를 먼저 읽고 단계별 checkbox를 실행한다. 각
> 단계의 command와 기대 결과를 기록하며, 승인되지 않은 중앙 BOM·release·merge
> 변경은 수행하지 않는다.

**Goal:** Timefold Solver 2.6.0의 dependency/migration, 증분 score,
SolverManager lifecycle, planning-list declarative shadow, Exposed 연동을
기존 workshop 모듈에서 재현 가능한 테스트와 한영 문서로 고정한다.

**Architecture:** 중앙 bluetape4k-dependencies BOM과 versionless catalog
alias를 유지하고, 기존 quickstart에는 실제 domain 회귀 검증을, shared 모듈에는
독립적인 list-variable element fixture를 둔다. school controller의 완료 상태는
작은 registry의 단일 상태 전이로 관리해 best/final/exception callback race를
차단한다.

**Tech Stack:** Kotlin 2.3+, Java 25+, Gradle Kotlin DSL, Timefold Solver
2.6.0, Spring Boot WebFlux 3.5.x, JUnit 5, MockK/Kluent 또는 bluetape4k
assertion helpers, Exposed JDBC/R2DBC.

---

## 파일 구조와 소유권

| 책임 | 생성/수정 파일 | 소유 범위 |
|---|---|---|
| migration/graph 근거 | docs/research/2026-09-14-timefold-2.6-migration.md | docs/research |
| 설계/계획/검토 | docs/superpowers/**, docs/review/** | docs |
| catalog 정리 | buildSrc/src/main/kotlin/Libs.kt (미사용일 때만) | buildSrc |
| list-shadow 예제 | 00-shared/bluetape4k-timefold/src/main/kotlin/io/bluetape4k/timefold/listshadow/ListShadowDomain.kt | 00-shared |
| list-shadow 테스트 | 00-shared/bluetape4k-timefold/src/test/kotlin/io/bluetape4k/timefold/listshadow/ListShadowDomainTest.kt | 00-shared |
| school 증분 score | 01-quickstarts/school-timetabling/src/test/kotlin/timefold/workshop/school/timetabling/solver/TimetableIncrementalScoreTest.kt | school |
| bed 증분 score | 01-quickstarts/bed-allocation/src/test/kotlin/timefold/workshop/bed/allocation/solver/BedAllocationIncrementalScoreTest.kt | bed |
| lifecycle registry | 01-quickstarts/school-timetabling/src/main/kotlin/timefold/workshop/school/timetabling/controller/TimetableJobRegistry.kt | school controller |
| lifecycle integration | 01-quickstarts/school-timetabling/src/main/kotlin/timefold/workshop/school/timetabling/controller/TimetableController.kt | school controller |
| lifecycle tests | 01-quickstarts/school-timetabling/src/test/kotlin/timefold/workshop/school/timetabling/controller/TimetableJobRegistryTest.kt, TimetableControllerTest.kt | school controller |
| persistence/docs parity | exposed/**, README.md, README.ko.md, existing module README files | Exposed/root/docs |
| durable lesson | docs/lessons/2026-09-14-issue-54-timefold-2.6.md | docs/lessons |

새 module, 새 dependency, workflow YAML, central BOM 파일은 이 계획의 쓰기
범위가 아니다.

## Task 1: dependency graph와 2.2→2.6 migration 근거 고정

**Files:**

- Create: docs/research/2026-09-14-timefold-2.6-migration.md
- Modify: buildSrc/src/main/kotlin/Libs.kt only after the no-reference check
- Test/verify: gradle/libs.versions.toml, root build.gradle.kts

- [x] **Step 1: 현재 source와 stale helper의 참조를 확인한다.**

    rg -n "timefold_solver|timefoldSolver\(" buildSrc 00-shared 01-quickstarts exposed
    rg -n "timefold\.solver\.(core|jackson|spring\.boot\.starter|benchmark)" gradle build.gradle.kts 00-shared 01-quickstarts exposed

Expected: 실제 build files는 libs.timefold.solver.*만 사용하고,
Libs.kt의 Versions.timefold_solver = "1.32.0"과 timefold_solver_* alias는
선언부만 남는다. 참조가 발견되면 삭제하지 않고 catalog alias 이전 설계로
돌아간다.

- [x] **Step 2: 이전 기준과 현재 graph를 저장한다.**

    git show e47496a:gradle/libs.versions.toml > /tmp/issue-54-catalog-2.2.toml
    ./gradlew :school-timetabling:dependencies --configuration runtimeClasspath
    ./gradlew :school-timetabling:dependencyInsight --dependency timefold-solver-core --configuration runtimeClasspath
    ./gradlew :school-timetabling:dependencyInsight --dependency timefold-solver-spring-boot-starter --configuration runtimeClasspath
    ./gradlew :school-timetabling:dependencyInsight --dependency timefold-solver-benchmark --configuration runtimeClasspath

Expected: core/jackson/starter는 2.6.0, benchmark insight는 No dependencies
matching given input were found다. 명령·HEAD·configuration·exit status를
migration 문서에 요약한다.

- [x] **Step 3: 공식 중간 버전 ledger를 작성한다.**

docs/research/2026-09-14-timefold-2.6-migration.md에 공식 release/upgrade
문서로 다음 표를 채운다.

    | 버전 | 공식 변경 | 현재 코드 영향 | 검증 | 상태 |
    |---|---|---|---|---|
    | 2.3 | 공식 v2.3 source의 정확한 변경 | symbol/config 영향 | command | 확인 |
    | 2.4 | 공식 v2.4 source의 정확한 변경 | symbol/config 영향 | command | 확인 |
    | 2.5 | 공식 v2.5 source의 정확한 변경 | symbol/config 영향 | command | 확인 |
    | 2.6 | 공식 v2.6 source의 정확한 변경 | symbol/config 영향 | command | 확인 |

최종 문서에 임시 placeholder를 남기지 않는다. 2.5/2.6만으로 2.3/2.4를
추론하면 task를 실패시킨다.

- [x] **Step 4: 참조가 없을 때만 stale alias를 삭제한다.**

삭제 대상은 Libs.kt의 Versions.timefold_solver 상수,
timefoldSolver(module, version) 함수와 그 함수로만 만들어지는
timefold_solver_bom부터 timefold_solver_webui까지의 값이다. libs.* catalog
alias와 root BOM import는 유지한다.

    ./gradlew buildSrc:compileKotlin
    ./gradlew :bluetape4k-timefold:compileKotlin
    git diff --check

Expected: buildSrc와 shared compile이 PASS한다. task 이름이 다르면
./gradlew tasks --all | rg compileKotlin으로 동등 task를 선택한다.

- [x] **Step 5: dependency governance를 확인한다.**

    scripts/sync-managed-catalog.py --check --summary
    scripts/sync-shared-versions.py --workspace .. --check --summary
    scripts/sync-dependabot-ignores.py --workspace .. --check --summary
    ./gradlew :school-timetabling:dependencies --configuration runtimeClasspath

drift가 있으면 중앙 BOM을 편집하지 말고 원인과 승인 범위를 기록한다.

## Task 2: school join/filter 증분 score 회귀를 RED로 고정

**Files:**

- Create: 01-quickstarts/school-timetabling/src/test/kotlin/timefold/workshop/school/timetabling/solver/TimetableIncrementalScoreTest.kt
- Modify: 01-quickstarts/school-timetabling/src/main/kotlin/timefold/workshop/school/timetabling/solver/TimetableConstraintProvider.kt only when RED reproduces a defect
- Reuse: existing TimetableConstraintProviderTest.kt, TimetableEnvironmentTest.kt, TimetableProvider

- [x] **Step 1: 작은 fixture와 FULL_ASSERT harness를 작성한다.**

    private fun smallTimetable(): Timetable {
        val monday = Timeslot("MONDAY", LocalTime.of(9, 0))
        val tuesday = Timeslot("TUESDAY", LocalTime.of(9, 0))
        val roomA = Room("Room A")
        val roomB = Room("Room B")
        return Timetable(
            name = "incremental-school",
            timeslots = listOf(monday, tuesday),
            rooms = listOf(roomA, roomB),
            lessons = listOf(
                Lesson("lesson-a", "math", "teacher-a", "group-a", monday, roomA),
                Lesson("lesson-b", "science", "teacher-a", "group-b", monday, null),
            ),
        )
    }

    private fun solveWithFullAssert(problem: Timetable): Timetable {
        val config = solverConfig.copyConfig()
            .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
            .withRandomSeed(0L)
            .withTerminationSpentLimit(Duration.ofMillis(100))
        return SolverFactory.create<Timetable>(config).buildSolver().solve(problem)
    }

실제 Timeslot, Room, Lesson constructor를 재사용하고 ID·입력 순서를 고정한다.

- [x] **Step 2: join entry/exit RED 테스트를 추가한다.**

    @Test
    fun teacherJoinEntersAndExitsWithoutStaleMatch() {
        val problem = smallTimetable()
        val solved = solveWithFullAssert(problem)
        solved.score.shouldNotBeNull()
        // ConstraintVerifier expectations are checked for a controlled joined
        // state (1 match) and an exited state (0 matches). The single
        // FULL_ASSERT solver run above is the incremental guard; do not rebuild
        // a new solver for each mutation.
    }

동일 방식으로 room join과 student-group join entry/exit를 추가한다. 각
fixture는 두 timeslot/room을 제공하고 random seed를 고정해 solver가 join
진입·이탈 move를 실제로 탐색하게 한다. score나 match assertion 없는 green
test는 PASS가 아니다. 각 controlled state의 match count는 기존
TimetableConstraintProviderTest의 ConstraintVerifier로 확인하되, 증분
consistency 주 증거는 한 번의 FULL_ASSERT solver run으로 남긴다.

    ./gradlew :school-timetabling:test --tests '*TimetableIncrementalScoreTest'

- [x] **Step 3: filter true/false transitions를 명시한다.**

teacherTimeEfficiency와 studentGroupSubjectVariety 각각에 대해 filter가
true인 fixture를 만든다. 관련 planning field 하나만 바꾸어 false로 만들고
기존 constraint weight에 따른 score 차이를 확인한 뒤 원상 복구한다.
ConstraintVerifier는 match count 보조 증거로만 사용하고 FULL_ASSERT 결과가
증분 일관성의 주 증거다.

- [x] **Step 4: RED가 실제 defect일 때만 최소 stream fix를 한다.**

stale/missing/duplicate match가 재현될 때만 TimetableConstraintProvider.kt의
해당 stream을 고치고 failing fixture를 보존한다. 재현되지 않으면 production
code를 변경하지 않고 migration evidence에
N/A: existing stream passed FULL_ASSERT transition을 기록한다.

## Task 3: bed join/filter 증분 score 회귀를 고정

**Files:**

- Create: 01-quickstarts/bed-allocation/src/test/kotlin/timefold/workshop/bed/allocation/solver/BedAllocationIncrementalScoreTest.kt
- Modify: 01-quickstarts/bed-allocation/src/main/kotlin/timefold/workshop/bed/allocation/solver/BedAllocationConstraintProvider.kt only on RED reproduction
- Reuse: BedAllocationConstraintProviderTest.kt, BedPlan, Stay

- [x] **Step 1: same-bed join fixture를 작성한다.**

두 stay를 같은 night에 고정하고 distinct bed에서 같은 bed로 이동시켜
sameBedInSameNight join에 들어가게 한다. 다시 원래 bed로 이동해 빠지는
경계를 검증한다. calculateSameNightCount의 실제 semantics를 사용한다.

- [x] **Step 2: unassigned/filter 경계를 작성한다.**

forEachIncludingUnassigned constraint 하나를 bed = null → bed != null로
검증하고, gender 또는 department filter 하나를 true → false → true로
검증한다. patient ID, 날짜, capacity는 고정한다.

- [x] **Step 3: FULL_ASSERT와 static verifier를 실행한다.**

    ./gradlew :bed-allocation:test --tests '*BedAllocationIncrementalScoreTest'

skip/disabled/static-only 결과는 PASS가 아니다.

## Task 4: SolverManager completion registry와 lifecycle tests

**Files:**

- Create: 01-quickstarts/school-timetabling/src/main/kotlin/timefold/workshop/school/timetabling/controller/TimetableJobRegistry.kt
- Modify: 01-quickstarts/school-timetabling/src/main/kotlin/timefold/workshop/school/timetabling/controller/TimetableController.kt
- Create: 01-quickstarts/school-timetabling/src/test/kotlin/timefold/workshop/school/timetabling/controller/TimetableJobRegistryTest.kt
- Modify: 01-quickstarts/school-timetabling/src/test/kotlin/timefold/workshop/school/timetabling/controller/TimetableControllerTest.kt

- [x] **Step 1: registry RED tests를 먼저 작성한다.**

registry API는 start(jobId, problem), recordBest(jobId, solution),
recordFinal(jobId, solution), recordFailure(jobId, error), get(jobId)로
고정한다. 상태는 PENDING, COMPLETED, FAILED 세 값이다.

    internal enum class CompletionState { PENDING, COMPLETED, FAILED }

    internal data class EntrySnapshot(
        val timetable: Timetable,
        val exception: Throwable?,
        val state: CompletionState,
    )

    internal class TimetableJobRegistry {
        private data class Entry(
            var timetable: Timetable,
            var exception: Throwable? = null,
            var state: CompletionState = CompletionState.PENDING,
            val lock: ReentrantLock = ReentrantLock(),
        )
        private val entries = ConcurrentHashMap<String, Entry>()

        fun start(jobId: String, problem: Timetable) {
            check(entries.putIfAbsent(jobId, Entry(problem)) == null) {
                "Duplicate solver jobId: $jobId"
            }
        }

        fun recordBest(jobId: String, solution: Timetable) {
            entries[jobId]?.let { entry -> entry.lock.withLock {
                entry.takeIf { it.state == CompletionState.PENDING }?.also {
                    it.timetable = solution
                }
            } }
        }

        fun recordFinal(jobId: String, solution: Timetable) {
            entries[jobId]?.let { entry -> entry.lock.withLock {
                entry.takeIf { it.state == CompletionState.PENDING }?.also {
                    it.timetable = solution
                    it.state = CompletionState.COMPLETED
                }
            } }
        }

        fun recordFailure(jobId: String, error: Throwable) {
            entries[jobId]?.let { entry -> entry.lock.withLock {
                entry.takeIf { it.state == CompletionState.PENDING }?.also {
                    it.exception = error
                    it.state = CompletionState.FAILED
                }
            } }
        }

        fun get(jobId: String): EntrySnapshot = entries[jobId]?.let {
            EntrySnapshot(it.timetable, it.exception, it.state)
        } ?: throw TimetableSolverException(jobId, HttpStatus.NOT_FOUND, "No timetable found")
    }

실제 구현은 check-then-set race가 없도록 entry별 lock 또는
ConcurrentHashMap.compute를 사용하고, public snapshot은 mutable Entry를
노출하지 않는다. recordBest는 PENDING일 때만 best를 갱신한다.

- [x] **Step 2: callback order와 duplicate tests를 RED로 확인한다.**

다음 여섯 cases를 CountDownLatch 또는 ExecutorService로 검증한다.

1. best → final은 final을 한 번 저장한다.
2. final → late best는 final을 유지한다.
3. best → failure는 failure를 한 번 저장한다.
4. failure → late final은 failure를 유지한다.
5. duplicate start는 예외를 내고 원래 problem을 유지한다.
6. concurrent final/failure는 terminal state 하나만 만든다.

    ./gradlew :school-timetabling:test --tests '*TimetableJobRegistryTest'

- [x] **Step 3: controller를 Timefold 2.6 final callback에 연결한다.**

기존 endpoint shape를 유지하면서 builder wiring을 다음 의미로 바꾼다.

    val jobId = UUID.randomUUID().toString()
    jobRegistry.start(jobId, problem)
    solverManager.solveBuilder()
        .withProblemId(jobId)
        .withProblemFinder { id -> jobRegistry.get(id.toString()).timetable }
        .withBestSolutionEventConsumer { event ->
            event.solution()?.let { jobRegistry.recordBest(jobId, it) }
        }
        .withFinalBestSolutionEventConsumer { event ->
            event.solution()?.let { jobRegistry.recordFinal(jobId, it) }
        }
        .withExceptionHandler { id, error ->
            jobRegistry.recordFailure(id.toString(), error)
            log.error(error) { "Solver failed for jobId: $id" }
        }
        .run()

getTimetable, getStatus, terminateSolving의 HTTP 계약은 유지한다. request
payload나 비밀을 로그로 남기지 않는다. late callback이 terminal snapshot을
덮어쓰지 않는지 registry에서 보장한다.

- [x] **Step 4: HTTP integration lifecycle을 보강한다.**

기존 DataSizeType solve test를 유지한다. 작은 fixture로 submit → NOT_SOLVING
poll → final score read를 검증하고, 두 번째 job에는 DELETE terminate를
호출해 결과가 수렴하는지 확인한다. Enterprise analyze test는 계속 disabled
상태이며 N/A로 보고한다.

    ./gradlew :school-timetabling:test --tests '*TimetableControllerTest'

## Task 5: shared planning-list element declarative shadow 예제

**Files:**

- Create: 00-shared/bluetape4k-timefold/src/main/kotlin/io/bluetape4k/timefold/listshadow/ListShadowDomain.kt
- Create: 00-shared/bluetape4k-timefold/src/test/kotlin/io/bluetape4k/timefold/listshadow/ListShadowDomainTest.kt

- [x] **Step 1: domain model을 추가한다.**

    @PlanningEntity
    class ListShadowRoute(
        @PlanningId val id: String,
        @PlanningListVariable(valueRangeProviderRefs = ["visitRange"])
        var visits: MutableList<ListShadowVisit> = mutableListOf(),
    )

    @PlanningEntity
    class ListShadowVisit(
        @PlanningId val id: String,
        val label: String,
    ) {
        @IndexShadowVariable(sourceVariableName = "visits")
        var indexInRoute: Int? = null

        @ShadowVariable(supplierName = "calculateSequencePosition")
        var sequencePosition: Int? = null

        @ShadowSources("indexInRoute")
        fun calculateSequencePosition(): Int? = indexInRoute?.plus(1)
    }

    @PlanningSolution
    data class ListShadowPlan(
        @PlanningEntityCollectionProperty
        val routes: List<ListShadowRoute>,
        @PlanningEntityCollectionProperty
        @ValueRangeProvider(id = "visitRange")
        val visits: List<ListShadowVisit>,
        @PlanningScore
        var score: SimpleScore? = null,
    )

두 entity class를 solver config에 명시적으로 등록한다. route가 solver-owned
mutable list를 소유하며 fixture factory는 fresh object를 반환한다.

- [x] **Step 2: reorder/move RED tests를 작성한다.**

    @Test
    fun indexAndDeclarativePositionRefreshAfterReorder() {
        val a = ListShadowVisit("a", "A")
        val b = ListShadowVisit("b", "B")
        val c = ListShadowVisit("c", "C")
        val route = ListShadowRoute("r1", mutableListOf(a, b, c))
        val solution = ListShadowPlan(listOf(route), listOf(a, b, c))

        SolutionManager.updateShadowVariables(solution)
        route.visits.map { it.sequencePosition } shouldBe listOf(1, 2, 3)

        route.visits = mutableListOf(c, a, b)
        SolutionManager.updateShadowVariables(solution)
        route.visits.map { it.sequencePosition } shouldBe listOf(1, 2, 3)
        c.sequencePosition shouldBe 1
        a.sequencePosition shouldBe 2
        b.sequencePosition shouldBe 3
    }

두 번째 test는 b를 empty route로 이동해 nullable index/position을 확인한다.
동일 solution으로 FULL_ASSERT smoke를 추가해 descriptor/entity registration을
검증한다. supplier는 indexInRoute만 읽고 field write나 ScoreDirector를
사용하지 않는다.

    ./gradlew :bluetape4k-timefold:test --tests '*ListShadowDomainTest'

## Task 6: Exposed persistence verification과 bilingual docs

**Files:**

- Modify: exposed JDBC/R2DBC tests only when a 2.6 regression reproduces
- Modify: README.md, README.ko.md
- Modify: 00-shared/bluetape4k-timefold/README.md
- Modify: 01-quickstarts/school-timetabling/README.md, README.adoc
- Modify: 01-quickstarts/bed-allocation/README.md, README.adoc

- [x] **Step 1: persistence tests를 순차 실행한다.**

    ./gradlew :exposed-jdbc-examples:test
    ./gradlew :exposed-r2dbc-examples:test

R2DBC H2 1.1.0.RELEASE와 H2 2.4.240 ABI pin은 변경하지 않는다. 필요한
Testcontainers는 Colima 상태를 확인한 뒤 backend 하나씩 실행한다.

- [x] **Step 2: root README 양쪽에 동일 실행 경로를 추가한다.**

    ./gradlew :bluetape4k-timefold:test --tests '*ListShadowDomainTest'
    ./gradlew :school-timetabling:test --tests '*TimetableIncrementalScoreTest'
    ./gradlew :school-timetabling:test --tests '*TimetableJobRegistryTest'
    ./gradlew :bed-allocation:test --tests '*BedAllocationIncrementalScoreTest'

Timefold 2.6 중앙 해결, community의 Enterprise ScoreAnalysis disabled/N/A,
Neighborhoods preview/custom move 미사용을 양쪽 문서에 설명한다.

- [x] **Step 3: existing module README를 code와 맞춘다.**

shared README에는 list-shadow annotation을, school/bed README pair에는
join/filter/lifecycle 명령을 적는다. locale 파일이 없으면 새 파일을 만들지
않고 root 한영 문서를 source of truth로 삼는다.

- [x] **Step 4: docs validation을 실행한다.**

    git diff --check
    rg -n "2\.6\.0|PlanningListVariable|ShadowVariable|SolverManager|Enterprise|Neighborhoods" README.md README.ko.md 00-shared/bluetape4k-timefold/README.md 01-quickstarts/school-timetabling/README.md 01-quickstarts/school-timetabling/README.adoc 01-quickstarts/bed-allocation/README.md 01-quickstarts/bed-allocation/README.adoc

## Task 7: integrated verification, review, lesson, commit

**Files:**

- Create: docs/review/2026-09-14-issue-54-implementation-review.md
- Create: docs/lessons/2026-09-14-issue-54-timefold-2.6.md

- [x] **Step 1: targeted-to-full verification을 dependency order로 실행한다.**

    ./gradlew :bluetape4k-timefold:test
    ./gradlew :school-timetabling:test :bed-allocation:test
    ./gradlew :exposed-jdbc-examples:test :exposed-r2dbc-examples:test
    ./gradlew test
    ./gradlew build
    scripts/verify-publication-poms.py --workspace .. --summary
    git diff --check

각 command의 exit code, exact HEAD, PASS/N/A/blocked를 기록한다. SKIPPED,
NO-SOURCE, disabled Enterprise, absent benchmark는 PASS가 아니다.

- [x] **Step 2: final 7-Tier review artifact를 작성한다.**

정확한 branch diff를 performance, stability, security, operator/ops,
developer/API, user/caller와 main integration 관점으로 검토한다. file:line,
P0-P3, disposition, SPW-01..05를 기록하고 P0=0, P1=0을 요구한다.

- [x] **Step 3: durable lesson을 작성한다.**

lesson에는 context, decision, migration surprise/failure(이번 receipt/liveness
이슈 포함), outcome, exact verification, review misses, future guard를
기록한다. central BOM/versionless alias 보존과 Neighborhoods/benchmark N/A
결정을 명시한다.

- [x] **Step 4: Lore trailer를 포함해 commit한다.**

artifact commit이 implementation commit보다 앞서도록 하고 모든 commit은
feature branch에 둔다.

    git add docs/superpowers docs/review/2026-09-14-issue-54-implementation-review.md docs/research docs/lessons
    git commit -m "Timefold 2.6 검증 설계와 회귀 증거를 고정한다"

commit body에는 Constraint, Rejected, Confidence, Scope-risk, Directive,
Tested, Not-tested trailer를 포함한다. generated build/와 /tmp evidence는
commit하지 않는다.

## Traceability와 stop conditions

| Issue #54 requirement | Plan task | Required evidence |
|---|---|---|
| core/starter/jackson versions and every intermediate migration | 1 | graph outputs + four-version ledger |
| central ownership and stable BOM | 1 | catalog/Gradle/POM governance PASS |
| school/bed join/filter incremental regressions | 2–3 | FULL_ASSERT transitions + expected matches |
| SolverManager submit/shutdown/failure | 4 | registry race tests + HTTP lifecycle test |
| independent planning-list declarative shadow | 5 | reorder/move/updateShadowVariables tests |
| Neighborhoods API | 1, 6 | active-source rg N/A receipt and README boundary |
| Exposed persistence and Korean/English README | 6 | sequential JDBC/R2DBC + parity diff |

Stop and report PENDING if a graph row, test, POM, review lens, writer row, or
exact-head evidence is missing. BLOCKED is only for missing external authority or
a destructive choice; central BOM, release, merge, branch deletion, or
publication are never solved by assumption.

## Plan self-review

- Spec coverage: Tasks 1–6 cover all seven Issue #54 acceptance rows; Task 7
  covers final DoD, 7-Tier review, lesson, and evidence retention.
- Ordering: graph/ownership precedes code; tests precede implementation; docs
  consume verified behavior; final review follows all tests.
- Placeholder scan: no TODO/TBD/unnamed edge-case task remains. Migration table
  sample rows are instructions and must contain exact sources before commit.
- Type consistency: TimetableJobRegistry method names and ListShadow properties
  are identical in tests and controller wiring.
