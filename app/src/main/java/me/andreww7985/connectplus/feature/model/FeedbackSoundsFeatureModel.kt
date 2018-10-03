package me.andreww7985.connectplus.feature.model

import me.andreww7985.connectplus.speaker.SpeakerModel

class FeedbackSoundsFeatureModel(speaker: SpeakerModel) : BaseFeatureModel(speaker) {
    var feedbackSoundsEnabled = false

    fun setData(feedbackSounds: Boolean) {
        this.feedbackSoundsEnabled = feedbackSounds

        dataChanged()
    }
}