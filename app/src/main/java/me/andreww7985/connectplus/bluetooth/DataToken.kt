package me.andreww7985.connectplus.bluetooth

enum class DataToken(val value: Byte) {
    TOKEN_MAC(0x48.byte),
    TOKEN_AUDIO_SOURCE(0x47.byte),
    TOKEN_ACTIVE_CHANNEL(0x46.byte),
    TOKEN_LINKED_DEVICE_COUNT(0x45.byte),
    TOKEN_BATTERY_STATUS(0x44.byte),
    TOKEN_COLOR(0x43.byte),
    TOKEN_MODEL(0x42.byte),
    TOKEN_NAME(0xC1.byte);

    companion object {
        fun from(value: Byte) = values().firstOrNull { it.value == value }
    }
}