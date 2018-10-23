package me.andreww7985.connectplus.manager

import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.Event
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.speaker.SpeakerModel
import timber.log.Timber

object SpeakerManager : BaseModel {
    var mainSpeaker: SpeakerModel? = null
    var selectedSpeaker: SpeakerModel? = null

    val speakers = HashMap<String, SpeakerModel>()

    val speakerFoundEvent = Event()

    fun speakerFound(speaker: SpeakerModel) {
        Timber.d("speakerFound")

        speakers[speaker.mac] = speaker

        if (speakers.size == 1) {
            Timber.d("speakerFound found main speaker MAC = ${speaker.mac}")
            mainSpeaker = speaker
            selectedSpeaker = speaker

            speaker.connectedEvent.subscribe {
                App.analytics.logSpeakerEvent("speaker_connected") {
                    putString("speaker_model", speaker.model.name)
                    putString("speaker_color", speaker.color.name)
                    putString("speaker_data", speaker.scanRecord)
                }

                if (speaker == mainSpeaker) {
                    UIHelper.openMainActivity()
                }

                unsubscribe()
            }
        }

        BleScanManager.stopScan()
        speakerFoundEvent.fire()
    }

    fun getSpeaker(index: Int): SpeakerModel? {
        return speakers
                .filter { (_, v) -> v.index == index }
                .flatMap { (_, v) -> listOf(v) }
                .firstOrNull()
    }
}
