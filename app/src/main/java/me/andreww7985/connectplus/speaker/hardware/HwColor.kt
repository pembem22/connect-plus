package me.andreww7985.connectplus.speaker.hardware

enum class HwColor {
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
    ECO_GREEN,
    ECO_BLUE,
    BLACKSTAR,
    TOMORROWLAND,
    GUN_METAL,
    UNKNOWN;


    companion object {
        fun from(speakerModel: HwModel, speakerColor: Int): HwColor {
            return when (speakerModel) {
                HwModel.CHARGE3 -> when (speakerColor) {
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
                HwModel.FLIP3 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> RED
                    3 -> ORANGE
                    4 -> PINK
                    5 -> GRAY
                    6 -> BLUE
                    7 -> YELLOW
                    8 -> TEAL
                    9 -> MALTA
                    10 -> SQUAD
                    11 -> ZAP
                    12 -> TRIO
                    13 -> MOSAIC
                    else -> UNKNOWN
                }
                HwModel.FLIP4 -> when (speakerColor) {
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
                HwModel.FLIP5 -> when (speakerColor) {
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
                    0x11 -> BLACKSTAR
                    0x12 -> TOMORROWLAND
                    0x13 -> SQUAD
                    0x14 -> ECO_BLUE
                    0x15 -> ECO_GREEN
                    0x16 -> CAMO
                    else -> UNKNOWN
                }
                HwModel.BOOMBOX -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> GREEN
                    8 -> SQUAD
                    else -> UNKNOWN
                }
                HwModel.BOOMBOX2 -> when (speakerColor) {
                    0, 1 -> BLACK
                    8 -> SQUAD
                    else -> UNKNOWN
                }
                HwModel.PULSE2 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> WHITE
                    else -> UNKNOWN
                }
                HwModel.PULSE3 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> WHITE
                    else -> UNKNOWN
                }
                HwModel.XTREME -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> RED
                    3, 6 -> BLUE
                    4 -> SQUAD
                    5 -> MALTA
                    else -> UNKNOWN
                }
                HwModel.XTREME2 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> RED
                    3 -> BLUE
                    4 -> TEAL
                    8 -> SQUAD
                    0x12 -> GUN_METAL
                    else -> UNKNOWN
                }
                HwModel.CHARGE4 -> when (speakerColor) {
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
                HwModel.PULSE4 -> when (speakerColor) {
                    0, 1 -> BLACK
                    2 -> WHITE
                    else -> UNKNOWN
                }
                HwModel.XTREME3 -> when (speakerColor) {
                    0, 1 -> BLACK
                    3 -> BLUE
                    7 -> GRAY
                    8 -> SQUAD
                    else -> UNKNOWN
                }
                HwModel.CHARGE5 -> when (speakerColor) {
                    1 -> BLACK
                    2 -> RED
                    3 -> BLUE
                    5 -> GREEN
                    7 -> GRAY
                    9 -> TEAL
                    0x10 -> PINK
                    0x13 -> SQUAD
                    else -> UNKNOWN
                }
                HwModel.UNKNOWN -> UNKNOWN
            }
        }
    }
}