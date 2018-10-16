package me.andreww7985.connectplus.speaker

enum class ProductColor {
    BLACK,
    BLUE,
    CAMO,
    DARK_BLUE,
    DESERT,
    GRAY,
    GREEN,
    MAGENTA,
    MALTA,
    MOSAIC,
    ORANGE,
    PINK,
    RED,
    SQUAD,
    TEAL,
    TRIO,
    YELLOW,
    WHITE,
    ZAP,
    UNKNOWN;


    companion object {
        fun from(speakerModel: ProductModel, speakerColor: Int): ProductColor {
            return when (speakerModel) {
                ProductModel.CHARGE3 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> RED
                    3 -> TEAL
                    4 -> BLUE
                    5 -> GRAY
                    6 -> MOSAIC
                    7 -> SQUAD
                    8 -> ZAP
                    9 -> MALTA
                    else -> UNKNOWN
                }
                ProductModel.FLIP3 -> when (speakerColor) {
                    2 -> RED
                    3 -> ORANGE
                    4 -> PINK
                    5 -> GRAY
                    6 -> BLUE
                    7 -> YELLOW
                    8 -> TEAL
                    else -> UNKNOWN
                }
                ProductModel.FLIP4 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> RED
                    3 -> BLUE
                    4 -> TEAL
                    5 -> GRAY
                    6 -> WHITE
                    7 -> MALTA
                    8 -> SQUAD
                    9 -> ZAP
                    0x10 -> TRIO
                    0x11 -> MOSAIC
                    0x12 -> DARK_BLUE
                    0x0A -> TRIO
                    0x0B -> MOSAIC
                    else -> UNKNOWN
                }
                ProductModel.BOOMBOX -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> GREEN
                    8 -> SQUAD
                    else -> UNKNOWN
                }
                ProductModel.PULSE3 -> when (speakerColor) {
                    1 -> BLACK
                    2 -> WHITE
                    else -> UNKNOWN
                }
                ProductModel.XTREME2 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> RED
                    3 -> BLUE
                    4 -> TEAL
                    else -> UNKNOWN
                }
                ProductModel.CHARGE4 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> RED
                    3 -> BLUE
                    4 -> WHITE
                    5 -> GREEN
                    6 -> YELLOW
                    7 -> GRAY
                    8 -> DESERT
                    9 -> TEAL
                    0x10 -> PINK
                    0x11 -> SQUAD
                    0x12 -> MAGENTA
                    0x13 -> CAMO
                    else -> UNKNOWN
                }
                else -> UNKNOWN
            }
        }
    }
}