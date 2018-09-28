package me.andreww7985.connectplus.hardware

import me.andreww7985.connectplus.bluetooth.b

enum class HardwareColor {
    BLACK,
    BLUE,
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
        fun from(speakerModel: HardwareModel, speakerColor: Byte): HardwareColor {
            return when (speakerModel) {
                HardwareModel.CHARGE3 -> when (speakerColor) {
                    0.b, 1.b -> BLACK
                    2.b -> RED
                    3.b -> TEAL
                    4.b -> BLUE
                    5.b -> GRAY
                    6.b -> MOSAIC
                    7.b -> SQUAD
                    8.b -> ZAP
                    9.b -> MALTA
                    else -> UNKNOWN
                }
                HardwareModel.FLIP3 -> when (speakerColor) {
                    2.b -> RED
                    3.b -> ORANGE
                    4.b -> PINK
                    5.b -> GRAY
                    6.b -> BLUE
                    7.b -> YELLOW
                    8.b -> TEAL
                    else -> UNKNOWN
                }
                HardwareModel.FLIP4 -> when (speakerColor) {
                    0.b, 1.b -> BLACK
                    2.b -> RED
                    3.b -> BLUE
                    4.b -> TEAL
                    5.b -> GRAY
                    6.b -> WHITE
                    7.b -> MALTA
                    8.b -> SQUAD
                    9.b -> ZAP
                    0x10.b -> TRIO
                    0x11.b -> MOSAIC
                    0x0A.b -> TRIO
                    0x0B.b -> MOSAIC
                    else -> UNKNOWN
                }
                HardwareModel.BOOMBOX -> when (speakerColor) {
                    0.b, 1.b -> BLACK
                    2.b -> GREEN
                    8.b -> SQUAD
                    else -> UNKNOWN
                }
                HardwareModel.PULSE3 -> when (speakerColor) {
                    1.b -> BLACK
                    2.b -> WHITE
                    else -> UNKNOWN
                }
                HardwareModel.XTREME2 -> when (speakerColor) {
                    0.b, 1.b -> BLACK
                    2.b -> RED
                    3.b -> BLUE
                    4.b -> TEAL
                    else -> UNKNOWN
                }
                HardwareModel.CHARGE4 -> when (speakerColor) {
                    0.b, 1.b -> BLACK
                    2.b -> RED
                    3.b -> BLUE
                    4.b -> WHITE
                    5.b -> GREEN
                    6.b -> YELLOW
                    7.b -> GRAY
                    8.b -> DESERT
                    9.b -> TEAL
                    0x10.b -> PINK
                    0x12.b -> MAGENTA
                    else -> UNKNOWN
                }
                else -> UNKNOWN
            }
        }
    }
}