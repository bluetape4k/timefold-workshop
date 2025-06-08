package timefold.workshop.bed.allocation.domain

/**
 * 숙소의 방([Room]) 배정할 때 성별에 대한 제한을 나타냅니다.
 */
enum class GenderLimitation(val code: String) {

    ANY_GENDER("N"),
    MALE_ONLY("M"),
    FEMALE_ONLY("F"),
    SAME_GENDER("S");

    companion object {
        fun ofCode(code: String): GenderLimitation? =
            entries.firstOrNull { it.code == code }
    }
}
