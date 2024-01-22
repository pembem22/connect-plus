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
            selectedSpeaker = speaker
        }

        BleScanManager.stopScan()
        speakerFoundEvent.fire()
    }

    fun speakerConnected(speaker: SpeakerModel) {
        Timber.d("speakerConnected MAC = ${speaker.mac}")

        App.analytics.logEvent("speaker_connected") {
            if (App.analytics.sendSpeakerData) {
                putString("speaker_model", speaker.hardware.model.name)
                putString("speaker_color", speaker.hardware.color.name)
                putString("speaker_platform", speaker.hardware.platform.name)
                putString("speaker_data", speaker.scanRecord)
            }
        }

        if (speaker == selectedSpeaker) {
            UIHelper.openMainActivity()
        }

        linkUpdated()
    }

    fun linkUpdated() {
        speakerList.clear()
        speakerList.addAll(speakers.values.filter { it.isDiscovered })

        linkUpdatedEvent.fire()
    }
}
