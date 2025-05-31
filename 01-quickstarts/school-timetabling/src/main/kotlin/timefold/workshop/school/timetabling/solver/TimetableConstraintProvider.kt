package timefold.workshop.school.timetabling.solver

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import ai.timefold.solver.core.api.score.stream.Joiners
import io.bluetape4k.timefold.solver.core.api.score.stream.forEach
import io.bluetape4k.timefold.solver.core.api.score.stream.forEachUniquePair
import io.bluetape4k.timefold.solver.core.api.score.stream.uni.join
import timefold.workshop.school.timetabling.domain.Lesson
import timefold.workshop.school.timetabling.solver.justifications.RoomConflictJustification
import timefold.workshop.school.timetabling.solver.justifications.StudentGroupConflictJustification
import timefold.workshop.school.timetabling.solver.justifications.TeacherConflictJustification
import timefold.workshop.school.timetabling.solver.justifications.TeacherRoomStabilityJustification
import timefold.workshop.school.timetabling.solver.justifications.TeacherTimeEfficiencyJustification
import java.time.Duration

class TimetableConstraintProvider: ConstraintProvider {

    /**
     * 학교 시간표 문제의 제약 조건을 정의합니다.
     *
     * 하드 제약 조건은 필수이며 반드시 충족되어야 하고, 소프트 제약 조건은 선호되지만 필수는 아닙니다.
     */
    override fun defineConstraints(constraintFactory: ConstraintFactory): Array<Constraint> =
        arrayOf(
            // Hard constraints
            roomConflict(constraintFactory),
            teacherConflict(constraintFactory),
            studentGroupConflict(constraintFactory),

            // Soft constraints
            teacherRoomStability(constraintFactory),
            teacherTimeEfficiency(constraintFactory),
            studentGroupSubjectVariety(constraintFactory)
        )

    // 교실은 동시에 하나의 수업만 수용할 수 있습니다.
    fun roomConflict(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEachUniquePair<Lesson>(
                Joiners.equal(Lesson::timeslot),
                Joiners.equal(Lesson::room)
            )
            .penalize(HardSoftScore.ONE_HARD)
            .justifyWith { lesson1: Lesson, lesson2: Lesson, score ->
                RoomConflictJustification(lesson1.room!!, lesson1, lesson2)
            }
            .asConstraint("Room conflict")
    }

    // 교사는 동시에 하나의 수업만 가르칠 수 있습니다.
    fun teacherConflict(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEachUniquePair<Lesson>(
                Joiners.equal(Lesson::timeslot),
                Joiners.equal(Lesson::teacher)
            )
            .penalize(HardSoftScore.ONE_HARD)
            .justifyWith { lesson1: Lesson, lesson2: Lesson, score ->
                TeacherConflictJustification(lesson1.teacher, lesson1, lesson2)
            }
            .asConstraint("Teacher conflict")
    }

    // 학생 그룹은 동시에 하나의 수업만 수강할 수 있습니다.
    fun studentGroupConflict(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEachUniquePair<Lesson>(
                Joiners.equal(Lesson::timeslot),
                Joiners.equal(Lesson::studentGroup)
            )
            .penalize(HardSoftScore.ONE_HARD)
            .justifyWith { lesson1: Lesson, lesson2: Lesson, _ ->
                StudentGroupConflictJustification(lesson1.studentGroup, lesson1, lesson2)
            }
            .asConstraint("Student group conflict")
    }

    // 교사가 같은 교실에서 수업을 가르치는 것을 선호합니다.
    fun teacherRoomStability(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEachUniquePair<Lesson>(
                Joiners.equal(Lesson::teacher)
            )
            .filter { lesson1, lesson2 -> lesson1.room !== lesson2.room }
            .penalize(HardSoftScore.ONE_SOFT)
            .justifyWith { lesson1: Lesson, lesson2: Lesson, _ ->
                // 교사가 같은 교실에서 수업을 가르치는 것을 선호합니다.
                // 예를 들어, "교사 John은 수업 Math와 Science를 가르치고 있습니다."
                TeacherRoomStabilityJustification(lesson1.teacher, lesson1, lesson2)
            }
            .asConstraint("Teacher room stability")
    }

    // 교사는 수업시간이 연속하는 것을 선호하고, 수업 시간이 비는 경우를 싫어합니다.
    fun teacherTimeEfficiency(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEach<Lesson>()
            .join(
                Joiners.equal(Lesson::teacher),
                Joiners.equal { lesson: Lesson -> lesson.timeslot?.dayOfWeek }
            )
            .filter { lesson1, lesson2 ->
                // 첫 번째 수업이 먼저 끝나고, 두 번째 수업이 나중에 시작되고,
                // 두 수업의 종료 시각과 시작 시각 사이의 간격이 30분 이하인 경우
                val between = Duration.between(
                    lesson1.timeslot?.endTime,
                    lesson2.timeslot?.startTime
                )
                !between.isNegative && between <= Duration.ofMinutes(30)
            }
            // 연속된 수업에 대해서는 보상을 주고, 비는 시간에 대해서는 패널티를 부여합니다.
            .reward(HardSoftScore.ONE_SOFT)
            .justifyWith { lesson1: Lesson, lesson2: Lesson, _ ->
                // 교사가 수업시간이 연속하는 것을 선호하고, 수업 시간이 비는 경우를 싫어합니다.
                // 예를 들어, "교사 John은 수업 Math와 Science를 가르치고 있습니다."
                TeacherTimeEfficiencyJustification(lesson1.teacher, lesson1, lesson2)
            }
            .asConstraint("Teacher time efficiency")
    }

    // 학생 그룹은 같은 과목의 연속 수업을 싫어합니다.
    fun studentGroupSubjectVariety(constraintFactory: ConstraintFactory): Constraint {
        return constraintFactory
            .forEach<Lesson>()
            .join(
                Joiners.equal(Lesson::subject),
                Joiners.equal(Lesson::studentGroup),
                Joiners.equal { lesson: Lesson -> lesson.timeslot?.dayOfWeek }
            )
            .filter { lesson1, lesson2 ->
                // 첫 번째 수업이 먼저 끝나고, 두 번째 수업이 나중에 시작되고,
                // 두 수업의 종료 시각과 시작 시각 사이의 간격이 30분 이하인 경우
                val between = Duration.between(
                    lesson1.timeslot?.endTime,
                    lesson2.timeslot?.startTime
                )
                !between.isNegative && between <= Duration.ofMinutes(30)
            }
            .penalize(HardSoftScore.ONE_SOFT)
            .justifyWith { lesson1: Lesson, lesson2: Lesson, _ ->
                // 학생 그룹은 같은 과목의 연속 수업을 싫어합니다.
                // 예를 들어, "학생 그룹 A는 수학 수업과 과학 수업을 듣고 있습니다."
                StudentGroupConflictJustification(lesson1.studentGroup, lesson1, lesson2)
            }
            .asConstraint("Student group subject variety")
    }
}
