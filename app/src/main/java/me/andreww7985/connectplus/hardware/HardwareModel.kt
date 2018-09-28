package me.andreww7985.connectplus.hardware

enum class HardwareModel(val value: String) {
    CHARGE3("1EBC"),
    CHARGE4("1F17"),
    FLIP3("ABCD"), // fix this
    FLIP4("1ED1"),
    PULSE3("1ED2"),
    BOOMBOX("1EE7"),
    XTREME2("1EFC"),
    UNKNOWN("0000");

    companion object {
        fun from(value: String) = values().firstOrNull { it.value == value } ?: UNKNOWN
    }
}