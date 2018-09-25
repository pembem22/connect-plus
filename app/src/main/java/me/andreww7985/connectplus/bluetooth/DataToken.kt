package me.andreww7985.connectplus.bluetooth

enum class DataToken(val value: Byte) {
    TOKEN_MAC(0x48.b),
    TOKEN_AUDIO_SOURCE(0x47.b),
    TOKEN_ACTIVE_CHANNEL(0x46.b),
    TOKEN_LINKED_DEVICE_COUNT(0x45.b),
    TOKEN_BATTERY_STATUS(0x44.b),
    TOKEN_COLOR(0x43.b),
    TOKEN_MODEL(0x42.b),
    TOKEN_NAME(0xC1.b);

    companion object {
        fun from(value: Byte) = values().firstOrNull { it.value == value }
    }
}