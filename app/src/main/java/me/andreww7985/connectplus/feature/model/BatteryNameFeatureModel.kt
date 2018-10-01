package me.andreww7985.connectplus.feature.model

import me.andreww7985.connectplus.speaker.SpeakerModel

class BatteryNameFeatureModel(speaker: SpeakerModel) : BaseFeatureModel(speaker) {
    lateinit var name: String
    var batteryLevel = -1
    var batteryCharging = false

    fun setData(batteryCharging: Boolean, batteryLevel: Int, name: String) {
        this.batteryCharging = batteryCharging
        this.batteryLevel = batteryLevel
        this.name = name

        dataChanged()
    }
}