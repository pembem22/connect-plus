package me.andreww7985.connectplus.protocol

enum class AudioChannel(val value: Int) {
    LEFT(1),
    RIGHT(2),
    STEREO(0);

    companion object {
        fun from(value: Int) = values().firstOrNull { it.value == value }
                ?: throw IllegalArgumentException("wrong audio channel value")
    }
}