package me.andreww7985.connectplus.protocol

enum class PacketType(val id: Int) {
    ACK(0x00),

    REQ_SPEAKER_INFO(0x11),
    RES_SPEAKER_INFO(0x12),
    SET_SPEAKER_INFO(0x15),

    PLAY_SOUND(0x31),

    REQ_DISCONNECT(0x35),

    REQ_FIRMWARE_VERSION(0x41),
    RES_FIRMWARE_VERSION(0x42),

    REQ_DFU_START(0x43),
    SET_DFU_DATA(0x44),
    RES_DFU_STATUS_CHANGE(0x45),
    REQ_DFU_CANCEL(0x47),

    REQ_FEEDBACK_SOUNDS(0x65),
    RES_FEEDBACK_SOUNDS(0x66),
    SET_FEEDBACK_SOUNDS(0x67),

    REQ_SPEAKERPHONE_MODE(0x68),
    RES_SPEAKERPHONE_MODE(0x69),
    SET_SPEAKERPHONE_MODE(0x70),

    REQ_BASS_VOLUME(0x77),
    RES_BASS_VOLUME(0x78),
    SET_BASS_VOLUME(0x76),

    REQ_ANALYTICS_DATA(0x81),

    UNKNOWN(-1);

    companion object {
        fun from(value: Byte) = values().firstOrNull { it.id == value.toInt() and 0xFF } ?: UNKNOWN
    }
}