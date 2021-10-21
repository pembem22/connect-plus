package me.andreww7985.connectplus.speaker.hardware

enum class HwModel(val modelName: String) {
    CHARGE3("charge3"),
    CHARGE4("charge4"),
    CHARGE5("charge5"),

    FLIP3("flip3"),
    FLIP4("flip4"),
    FLIP5("flip5"),
    FLIP6("flip6"),

    XTREME("xtreme"),
    XTREME2("xtreme2"),
    XTREME3("xtreme3"),

    PULSE2("pulse2"),
    PULSE3("pulse3"),
    PULSE4("pulse4"),
    PULSE5("pulse5"),

    BOOMBOX("boombox"),
    BOOMBOX2("boombox2"),

    UNKNOWN("unknown");

    companion object {
        fun from(modelId: Int) = when (modelId) {
            0x0023 /* CSR */ -> FLIP3
            0x0024 /* CSR */ -> XTREME
            0x0026 /* CSR */ -> PULSE2
            0x1EBC /* CSR */, 0x1F25 /* QCC - cancelled */ -> CHARGE3
            0x1ED1 /* CSR */, 0x1F24 /* QCC - cancelled */ -> FLIP4
            0x1ED2 /* CSR */, 0x1F28 /* QCC - cancelled */ -> PULSE3
            0x1EE7 /* CSR */, 0x1F27 /* QCC */ -> BOOMBOX
            0x1EFC /* CSR */, 0x1F26 /* QCC */, 0x2038 /* QCC - Gun Metal */ -> XTREME2
            0x1F17 /* CSR */, 0x1F29 /* QCC */ -> CHARGE4
            0x1F31 /* VIMICRO */ -> FLIP5
            0x1F53 /* VIMICRO */ -> BOOMBOX2
            0x1F56 /* VIMICRO */ -> PULSE4
            0x202F /* VIMICRO */ -> XTREME3
            0x2040 /* VIMICRO */ -> CHARGE5
            0x204FF /* VIMICRO */ -> FLIP6
            0x2050F /* VIMICRO */ -> PULSE5
            else -> UNKNOWN
        }
    }
}