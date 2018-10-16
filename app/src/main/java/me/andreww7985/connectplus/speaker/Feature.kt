package me.andreww7985.connectplus.speaker

abstract class Feature {
    enum class Type(val clazz: Class<out Feature>) {
        BATTERY_NAME(BatteryName::class.java),
        FEEDBACK_SOUNDS(FeedbackSounds::class.java),
        FIRMWARE_VERSION(FirmwareVersion::class.java),
        SPEAKERPHONE_MODE(SpeakerphoneMode::class.java)
    }

    class BatteryName : Feature() {
        var batteryLevel: Int? = null
        var batteryCharging: Boolean? = null
        var deviceName: String? = null
    }

    class FeedbackSounds : Feature() {
        var enabled: Boolean? = null
    }

    class FirmwareVersion : Feature() {
        var major: Int? = null
        var minor: Int? = null
        var build: Int? = null
    }

    class SpeakerphoneMode : Feature() {
        var enabled: Boolean? = null
    }
}