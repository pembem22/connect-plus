package me.andreww7985.connectplus.bluetooth

enum class ActiveChannel(val value: Byte) {
    LEFT(1),
    RIGHT(2),
    BOTH(0);

    companion object {
        fun from(value: Byte) = values().firstOrNull { it.value == value }
    }
}