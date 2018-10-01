package me.andreww7985.connectplus.feature.model

import me.andreww7985.connectplus.speaker.SpeakerModel

class FirmwareVersionFeatureModel(speaker: SpeakerModel) : BaseFeatureModel(speaker) {
    var firmwareMajor = -1
    var firmwareMinor = -1
    var firmwareBuild = -1

    fun setData(firmwareMajor: Int, firmwareMinor: Int, firmwareBuild: Int) {
        this.firmwareMajor = firmwareMajor
        this.firmwareMinor = firmwareMinor
        this.firmwareBuild = firmwareBuild

        dataChanged()
    }
}