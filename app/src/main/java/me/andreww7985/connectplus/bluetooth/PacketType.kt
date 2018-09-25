package me.andreww7985.connectplus.bluetooth

enum class PacketType(val value: Byte) {
    ACK(0x00.b),

    REQ_SPEAKER_INFO(0x11.b),
    RES_SPEAKER_INFO(0x12.b),
    SET_SPEAKER_INFO(0x15.b),

    PLAY_SOUND(0x31.b),

    REQ_DISCONNECT(0x35.b),

    REQ_FIRMWARE_VERSION(0x41.b),
    RES_FIRMWARE_VERSION(0x42.b),

    REQ_DFU_START(0x43.b),
    SET_DFU_DATA(0x44.b),
    RES_DFU_STATUS_CHANGE(0x45.b),
    REQ_DFU_CANCEL(0x47.b),

    REQ_AUDIO_FEEDBACK(0x65.b),
    RES_AUDIO_FEEDBACK(0x66.b),
    SET_AUDIO_FEEDBACK(0x67.b),

    REQ_ANALYTICS_DATA(0x81.b);

    companion object {
        fun from(value: Byte) = values().firstOrNull { it.value == value }
    }
}