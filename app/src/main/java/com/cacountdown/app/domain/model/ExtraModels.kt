package com.cacountdown.app.domain.model

enum class CaCourse(val stored: String, val label: String) {
    FOUNDATION("FOUNDATION", "CA Foundation"),
    INTERMEDIATE("INTER", "CA Intermediate"),
    FINAL("FINAL", "CA Final"),
    ;

    companion object {
        fun fromStored(value: String): CaCourse = entries.firstOrNull { it.stored == value } ?: INTERMEDIATE
    }
}

enum class GoalUnit(val label: String) {
    HOURS("hours"),
    CHAPTERS("chapters"),
    REVISIONS("revisions"),
    MOCK_TESTS("mock tests"),
}
