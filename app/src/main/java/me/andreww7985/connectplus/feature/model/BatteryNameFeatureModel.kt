package me.andreww7985.connectplus.feature.model

import me.andreww7985.connectplus.speaker.SpeakerModel

class BatteryNameFeatureModel(speaker: SpeakerModel) : BaseFeatureModel(speaker) {
    lateinit var name: String
    var batteryLevel = -1
    var batteryCharging = false

    fun loadData(forceLoad: Boolean, callback: (() -> Unit)) {
        if (batteryLevel == -1 || forceLoad) {
            // TODO: Load REAL data
            batteryLevel = 100
            batteryCharging = true
            name = "JBL name"
        }

        callback()
    }
}