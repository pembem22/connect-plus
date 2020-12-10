package me.andreww7985.connectplus.speaker.hardware

enum class HwPlatform {
    CSR,
    QCC,
    VIMICRO,
    UNKNOWN;

    companion object {
        fun from(modelId: Int) = when (modelId) {
            0x0023, 0x0024, 0x0026, 0x1EBC, 0x1ED1, 0x1ED2, 0x1EE7, 0x1EFC, 0x1F17 -> CSR
            0x1F25, 0x1F24, 0x1F28, 0x1F27, 0x1F26, 0x1F29, 0x2038 -> QCC
            0x1F31, 0x1F53, 0x1F56, 0x202F -> VIMICRO
            else -> UNKNOWN
        }
    }
}