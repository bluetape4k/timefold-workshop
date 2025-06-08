package timefold.workshop.bed.allocation.solver

import ai.timefold.solver.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore
import ai.timefold.solver.core.api.score.stream.Constraint
import ai.timefold.solver.core.api.score.stream.ConstraintFactory
import ai.timefold.solver.core.api.score.stream.ConstraintProvider
import ai.timefold.solver.core.api.score.stream.Joiners
import io.bluetape4k.timefold.solver.core.api.score.stream.forEach
import io.bluetape4k.timefold.solver.core.api.score.stream.forEachIncludingUnassigned
import io.bluetape4k.timefold.solver.core.api.score.stream.forEachUniquePair
import timefold.workshop.bed.allocation.domain.Department
import timefold.workshop.bed.allocation.domain.Gender
import timefold.workshop.bed.allocation.domain.GenderLimitation
import timefold.workshop.bed.allocation.domain.Stay
import java.util.function.Function

class BedAllocationConstraintProvider: ConstraintProvider {

    override fun defineConstraints(
        constraintFactory: ConstraintFactory,
    ): Array<out Constraint?> = arrayOf(
        sameBedInSameNight(constraintFactory),
        femaleInMaleRoom(constraintFactory),
        maleInFemaleRoom(constraintFactory),
        differentGenderInSameGenderRoomIfSameNight(constraintFactory),
        departmentMinimumAge(constraintFactory),
        departmentMaximumAge(constraintFactory),
        requiredPatientEquipment(constraintFactory),
        assignEveryPatientToABed(constraintFactory),
        preferredMaximumCapacity(constraintFactory),
        departmentSpecialty(constraintFactory),
        departmentSpecialtyNotFirstPriority(constraintFactory),
        preferredPatientEquipment(constraintFactory),
    )

    fun sameBedInSameNight(cf: ConstraintFactory): Constraint {
        return cf
            .forEachUniquePair<Stay>(
                Joiners.equal(Stay::bed)
            )
            .filter { left, right ->
                left.calculateSameNightCount(right) > 0
            }
            .penalize(
                HardMediumSoftScore.ofHard(1000),
                Stay::calculateSameNightCount
            )
            .asConstraint("Same bed in same night")
    }

    fun femaleInMaleRoom(cf: ConstraintFactory): Constraint {
        return cf
            .forEachIncludingUnassigned<Stay>()
            .filter { stay ->
                stay.patient!!.gender == Gender.FEMALE &&
                        stay.roomGenderLimitation == GenderLimitation.MALE_ONLY
            }
            .penalize(
                HardMediumSoftScore.ofHard(50),
                Stay::nightCount
            )
            .asConstraint("femaleInMaleRoom")
    }

    fun maleInFemaleRoom(cf: ConstraintFactory): Constraint {
        return cf
            .forEachIncludingUnassigned<Stay>()
            .filter { stay ->
                stay.patient?.gender == Gender.MALE &&
                        stay.roomGenderLimitation == GenderLimitation.FEMALE_ONLY
            }
            .penalize(
                HardMediumSoftScore.ofHard(50),
                Stay::nightCount
            )
            .asConstraint("maleInFemaleRoom")
    }

    fun differentGenderInSameGenderRoomIfSameNight(cf: ConstraintFactory): Constraint {
        return cf
            .forEach<Stay>()
            .filter { stay ->
                stay.roomGenderLimitation == GenderLimitation.SAME_GENDER
            }
            .join(
                cf.forEach<Stay>()
                    .filter { stay -> stay.roomGenderLimitation == GenderLimitation.SAME_GENDER },
                Joiners.equal(Stay::room),
                Joiners.lessThan(Stay::id),
                Joiners
                    .filtering { left, right ->
                        left.patient!!.gender != right.patient!!.gender &&
                                left.calculateSameNightCount(right) > 0
                    }
            )
            .penalize(
                HardMediumSoftScore.ofHard(1000),
                Stay::calculateSameNightCount
            )
            .asConstraint("differentGenderInSameGenderRoomInSameNight")
    }

    fun departmentMinimumAge(cf: ConstraintFactory): Constraint {
        return cf
            .forEachIncludingUnassigned<Department>()
            .filter { it.minimumAge != Int.MIN_VALUE }
            .join(
                cf.forEachIncludingUnassigned<Stay>(),
                Joiners.equal(Function.identity(), Stay::department),
                Joiners.greaterThan(Department::minimumAge, Stay::patientAge)
            )
            .penalize(HardMediumSoftScore.ofHard(100)) { dept, stay ->
                stay.nightCount
            }
            .asConstraint("departmentMinimumAge")
    }

    fun departmentMaximumAge(cf: ConstraintFactory): Constraint {
        return cf
            .forEachIncludingUnassigned<Department>()
            .filter { it.maximumAge != Int.MAX_VALUE }
            .join(
                cf.forEachIncludingUnassigned<Stay>(),
                Joiners.equal(Function.identity(), Stay::department),
                Joiners.lessThan(Department::maximumAge, Stay::patientAge)
            )
            .penalize(HardMediumSoftScore.ofHard(100)) { department, stay ->
                stay.nightCount
            }
            .asConstraint("departmentMaximumAge")
    }

    fun requiredPatientEquipment(cf: ConstraintFactory): Constraint {
        return cf
            .forEach<Stay>()
            .filter { stay -> stay.room?.equipments?.containsAll(stay.patientRequiredEquipments) != true }
            .penalize(HardMediumSoftScore.ofHard(50)) { stay ->
                stay.nightCount *
                        stay.patientRequiredEquipments.count { equipment ->
                            stay.room?.equipments?.contains(equipment) == true
                        }
            }
            .asConstraint("requiredPatientEquipment")
    }

    // Medium
    fun assignEveryPatientToABed(cf: ConstraintFactory): Constraint {
        return cf
            .forEachIncludingUnassigned<Stay>()
            .filter { it.bed == null }
            .penalize(
                HardMediumSoftScore.ONE_MEDIUM,
                Stay::nightCount
            )
            .asConstraint("assignEveryPatientToABed")
    }

    // Soft
    fun preferredMaximumCapacity(cf: ConstraintFactory): Constraint {
        return cf
            .forEach<Stay>()
            .filter { stay ->
                val maxCapacity = stay.patientPreferredMaximumCapacity ?: Int.MAX_VALUE
                val roomCapacity = stay.room?.capacity ?: Int.MIN_VALUE
                maxCapacity < roomCapacity
            }
            .penalize(
                HardMediumSoftScore.ofSoft(8),
                Stay::nightCount
            )
            .asConstraint("preferredMaximumCapacity")
    }

    fun departmentSpecialty(cf: ConstraintFactory): Constraint {
        return cf
            .forEach<Stay>()
            .filter { !it.hasDepartmentSpecialty }
            .penalize(
                HardMediumSoftScore.ofSoft(10),
                Stay::nightCount
            )
            .asConstraint("departmentSpecialty")
    }

    fun departmentSpecialtyNotFirstPriority(cf: ConstraintFactory): Constraint {
        return cf
            .forEach<Stay>()
            .filter { it.specialtyPriority > 1 }
            .penalize(HardMediumSoftScore.ofSoft(10)) { stay ->
                (stay.specialtyPriority - 1) * stay.nightCount
            }
            .asConstraint("departmentSpecialtyNotFirstPriority")
    }

    fun preferredPatientEquipment(cf: ConstraintFactory): Constraint {
        return cf
            .forEach<Stay>()
            .filter { stay ->
                stay.room?.equipments?.containsAll(stay.patientPreferredEquipments) != true
            }
            .penalize(HardMediumSoftScore.ofHard(50)) { stay ->
                val equipmentCount = stay.patientPreferredEquipments.count { equipment ->
                    stay.room?.equipments?.contains(equipment) == true
                }
                stay.nightCount * equipmentCount
            }
            .asConstraint("preferredPatientEquipment")
    }
}
