package me.andreww7985.connectplus.speaker

enum class ProductModel(val value: String) {
    CHARGE3("1EBC"),
    CHARGE4("1F17"),
    FLIP3("0023"),
    FLIP4("1ED1"),
    PULSE2("0026"),
    PULSE3("1ED2"),
    BOOMBOX("1EE7"),
    XTREME("0024"),
    XTREME2("1EFC"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun from(value: String) = values().firstOrNull { it.value == value } ?: UNKNOWN
    }
}