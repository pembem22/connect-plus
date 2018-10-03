package me.andreww7985.connectplus.hardware

import me.andreww7985.connectplus.bluetooth.byte

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
                    0.byte, 1.byte -> BLACK
                    2.byte -> RED
                    3.byte -> TEAL
                    4.byte -> BLUE
                    5.byte -> GRAY
                    6.byte -> MOSAIC
                    7.byte -> SQUAD
                    8.byte -> ZAP
                    9.byte -> MALTA
                    else -> UNKNOWN
                }
                HardwareModel.FLIP3 -> when (speakerColor) {
                    2.byte -> RED
                    3.byte -> ORANGE
                    4.byte -> PINK
                    5.byte -> GRAY
                    6.byte -> BLUE
                    7.byte -> YELLOW
                    8.byte -> TEAL
                    else -> UNKNOWN
                }
                HardwareModel.FLIP4 -> when (speakerColor) {
                    0.byte, 1.byte -> BLACK
                    2.byte -> RED
                    3.byte -> BLUE
                    4.byte -> TEAL
                    5.byte -> GRAY
                    6.byte -> WHITE
                    7.byte -> MALTA
                    8.byte -> SQUAD
                    9.byte -> ZAP
                    0x10.byte -> TRIO
                    0x11.byte -> MOSAIC
                    0x0A.byte -> TRIO
                    0x0B.byte -> MOSAIC
                    else -> UNKNOWN
                }
                HardwareModel.BOOMBOX -> when (speakerColor) {
                    0.byte, 1.byte -> BLACK
                    2.byte -> GREEN
                    8.byte -> SQUAD
                    else -> UNKNOWN
                }
                HardwareModel.PULSE3 -> when (speakerColor) {
                    1.byte -> BLACK
                    2.byte -> WHITE
                    else -> UNKNOWN
                }
                HardwareModel.XTREME2 -> when (speakerColor) {
                    0.byte, 1.byte -> BLACK
                    2.byte -> RED
                    3.byte -> BLUE
                    4.byte -> TEAL
                    else -> UNKNOWN
                }
                HardwareModel.CHARGE4 -> when (speakerColor) {
                    0.byte, 1.byte -> BLACK
                    2.byte -> RED
                    3.byte -> BLUE
                    4.byte -> WHITE
                    5.byte -> GREEN
                    6.byte -> YELLOW
                    7.byte -> GRAY
                    8.byte -> DESERT
                    9.byte -> TEAL
                    0x10.byte -> PINK
                    0x12.byte -> MAGENTA
                    else -> UNKNOWN
                }
                else -> UNKNOWN
            }
        }
    }
}