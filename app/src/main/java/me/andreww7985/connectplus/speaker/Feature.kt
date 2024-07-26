package me.andreww7985.connectplus.speaker

abstract class Feature {
    enum class Type(val clazz: Class<out Feature>) {
        BATTERY_NAME(BatteryName::class.java),
        FEEDBACK_SOUNDS(FeedbackSounds::class.java),
        FIRMWARE_VERSION(FirmwareVersion::class.java),
        SPEAKERPHONE_MODE(SpeakerphoneMode::class.java),
        BASS_LEVEL(BassLevel::class.java);

        companion object {
            fun fromClass(clazz: Class<out Feature>): Type = entries.first { type -> type.clazz == clazz }
        }
    }

    class BatteryName(var batteryLevel: Int, var batteryCharging: Boolean, var deviceName: String) : Feature()

    class FeedbackSounds(var enabled: Boolean) : Feature()

    class FirmwareVersion(var major: Int, var minor: Int, var build: Int?) : Feature()

    class SpeakerphoneMode(var enabled: Boolean) : Feature()

    class BassLevel(var level: Int) : Feature()
}