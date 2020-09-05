package me.andreww7985.connectplus.speaker

enum class ProductModel(val value: Int, val modelName: String) {
    FLIP3(0x0023, "flip3"),
    XTREME(0x0024, "xtreme"),
    PULSE2(0x0026, "pulse2"),

    CHARGE3(0x1EBC, "charge3"),
    FLIP4(0x1ED1, "flip4"),
    PULSE3(0x1ED2, "pulse3"),
    BOOMBOX(0x1EE7, "boombox"),
    XTREME2(0x1EFC, "xtreme2"),

    CHARGE4(0x1F17, "charge4"),

    FLIP4_QCC(0x1F24, "flip4"),
    CHARGE3_QCC(0x1F25, "charge3"),
    XTREME2_QCC(0x1F26, "xtreme2"),
    BOOMBOX_QCC(0x1F27, "boombox"),
    PULSE3_QCC(0x1F28, "pulse3"),
    CHARGE4_QCC(0x1F29, "charge4"),
    FLIP5(0x1F31, "flip5"),

    BOOMBOX2(0x1F53, "boombox2"),
    PULSE4(0x1F56, "pulse4"),

    UNKNOWN(0xFFFF, "unknown");

    fun getMtu() = when (this) {
        CHARGE3, CHARGE4, FLIP3, FLIP4, PULSE2, PULSE3, BOOMBOX, XTREME, XTREME2, UNKNOWN,
        CHARGE3_QCC, CHARGE4_QCC, PULSE3_QCC, BOOMBOX_QCC, XTREME2_QCC, FLIP4_QCC -> 517

        FLIP5, PULSE4, BOOMBOX2 -> 35
    }

    companion object {
        fun from(value: Int) = values().firstOrNull { it.value == value } ?: UNKNOWN
    }
}