package me.andreww7985.connectplus.manager

import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.Event
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.speaker.SpeakerModel
import timber.log.Timber

object SpeakerManager : BaseModel {
    // var mainSpeaker: SpeakerModel? = null

    var selectedSpeaker: SpeakerModel? = null

    val speakers = HashMap<String, SpeakerModel>()
    val speakerList = ArrayList<SpeakerModel>()

    val speakerFoundEvent = Event()
    val linkUpdatedEvent = Event()

    fun speakerFound(speaker: SpeakerModel) {
        Timber.d("speakerFound MAC = ${speaker.mac}")

        speakers[speaker.mac] = speaker

        if (speakers.size == 1) {
            Timber.d("speakerFound found main speaker")
            // mainSpeaker = speaker
            selectedSpeaker = speaker
        }

        speaker.connectedEvent.subscribe {
            speakerConnected(speaker)
            unsubscribe()
        }

        BleScanManager.stopScan()
        speakerFoundEvent.fire()
    }

    private fun speakerConnected(speaker: SpeakerModel) {
        Timber.d("speakerConnected MAC = ${speaker.mac}")

        App.analytics.logSpeakerEvent("speaker_connected") {
            putString("speaker_model", speaker.hardware.model.name)
            putString("speaker_color", speaker.hardware.color.name)
            putString("speaker_data", speaker.scanRecord)
        }

        if (speaker == selectedSpeaker) {
            UIHelper.openMainActivity()
        }

        linkUpdated()
    }

//    fun speakerDisconnected() {
//
//    }

    fun linkUpdated() {
        speakerList.clear()
        speakerList.addAll(speakers.values.filter { it.isDiscovered })

        linkUpdatedEvent.fire()
    }

//    fun getSpeaker(index: Int) = speakers.values.filter { it.index == index }.firstOrNull()
}
