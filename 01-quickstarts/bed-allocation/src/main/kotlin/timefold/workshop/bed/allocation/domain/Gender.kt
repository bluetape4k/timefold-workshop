package timefold.workshop.bed.allocation.domain

/**
 * 성별을 나타냅니다.
 */
enum class Gender(val code: String) {

    MALE("M"),
    FEMALE("F");

    companion object {
        fun ofCode(code: String): Gender? =
            entries.firstOrNull { it.code == code }
    }
}
