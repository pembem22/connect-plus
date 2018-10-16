package me.andreww7985.connectplus.bluetooth

enum class DataToken(val id: Int) {
    TOKEN_MAC(0x48),
    TOKEN_AUDIO_SOURCE(0x47),
    TOKEN_ACTIVE_CHANNEL(0x46),
    TOKEN_LINKED_DEVICE_COUNT(0x45),
    TOKEN_BATTERY_STATUS(0x44),
    TOKEN_COLOR(0x43),
    TOKEN_MODEL(0x42),
    TOKEN_NAME(0xC1);

    companion object {
        fun from(value: Int) = values().firstOrNull { it.id == value }
    }
}