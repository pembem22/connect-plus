package me.andreww7985.connectplus.speaker

enum class ProductModel(val value: Int) {
    CHARGE3(0x1EBC),
    CHARGE4(0x1F17),
    FLIP3(0x0023),
    FLIP4(0x1ED1),
    FLIP5(0x1F31),
    PULSE2(0x0026),
    PULSE3(0x1ED2),
    BOOMBOX(0x1EE7),
    XTREME(0x0024),
    XTREME2(0x1EFC),
    UNKNOWN(0xFFFF);

    companion object {
        fun from(value: Int) = values().firstOrNull { it.value == value } ?: UNKNOWN
    }
}