package me.andreww7985.connectplus.feature.model

import me.andreww7985.connectplus.speakers.Speaker

class BatteryNameFeatureModel(speaker: Speaker) : BaseFeatureModel(speaker) {
    lateinit var name: String
    var batteryLevel = -1
    var batteryCharging = false

    fun loadData(force: Boolean, callback: (() -> Unit)) {
        // TODO: Implement load using packets or tasks
        batteryLevel = speaker.batteryLevel
        batteryCharging = speaker.batteryCharging
        name = speaker.name

        callback()
    }
}