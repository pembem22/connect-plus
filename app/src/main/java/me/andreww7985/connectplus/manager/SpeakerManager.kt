package me.andreww7985.connectplus.manager

import android.util.Log
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.speaker.SpeakerModel

object SpeakerManager {
    private const val TAG = "SpeakerManager"
    var mainSpeaker: SpeakerModel? = null
    var selectedSpeaker: SpeakerModel? = null
        set(value) {
            Log.d(TAG, "setSelectedSpeaker $value")
            field = value
        }

    val speakers = HashMap<String, SpeakerModel>()

    fun speakerFound(speaker: SpeakerModel) {
        Log.d(TAG, "speakerFound")
        App.logSpeakerEvent("speaker_connected") {
            putString("speaker_model", speaker.model.name)
            putString("speaker_color", speaker.color.name)
            putString("speaker_data", speaker.scanRecord)
        }

        speakers[speaker.mac] = speaker

        if (speakers.size == 1) {
            Log.d(TAG, "speakerFound found main speaker MAC = ${speaker.mac}")
            mainSpeaker = speaker
            selectedSpeaker = speaker
        }

        BleScanManager.stopScan()
    }

    fun speakerConnected(speaker: SpeakerModel) {
        Log.d(TAG, "speakerConnected")

        if (speaker == mainSpeaker) {
            UIHelper.openMainActivity()
        }


        BluetoothProtocol.requestSpeakerInfo(speaker)
    }

    fun getSpeaker(index: Int): SpeakerModel? {
        return speakers
                .filter { (_, v) -> v.index == index }
                .flatMap { (_, v) -> listOf(v) }
                .firstOrNull()
    }
}
