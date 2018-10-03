package me.andreww7985.connectplus.feature.model

import me.andreww7985.connectplus.speaker.SpeakerModel

class SpeakerphoneModeFeatureModel(speaker: SpeakerModel) : BaseFeatureModel(speaker) {
    var speakerphoneModeEnabled = false

    fun setData(speakerphoneMode: Boolean) {
        this.speakerphoneModeEnabled = speakerphoneMode

        dataChanged()
    }
}