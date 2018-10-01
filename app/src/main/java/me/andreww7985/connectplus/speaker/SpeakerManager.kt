package me.andreww7985.connectplus.speaker

import android.util.Log
import me.andreww7985.connectplus.ConnectPlusApp
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.bluetooth.BluetoothScanner
import me.andreww7985.connectplus.controller.FragmentController
import me.andreww7985.connectplus.helpers.UIHelper

object SpeakerManager {
    private const val TAG = "SpeakerManager"
    var mainSpeaker: SpeakerModel? = null
    var selectedSpeaker: SpeakerModel? = null
        set(value) {
            Log.d(TAG, "setSelectedSpeaker $value")
            field = value
        }

    //val connectPlusEnabled = false

    val speakers = ArrayList<SpeakerModel>()
    val leftSpeakers = ArrayList<SpeakerModel>()
    val rightSpeakers = ArrayList<SpeakerModel>()
    val stereoSpeakers = ArrayList<SpeakerModel>()

    fun onSpeakerFound(speaker: SpeakerModel) {
        Log.d(TAG, "onSpeakerFound")
        ConnectPlusApp.logSpeakerEvent("speaker_connected") {
            putString("speaker_model", speaker.model.name)
            putString("speaker_color", speaker.color.name)
            putString("speaker_data", speaker.scanRecord)
        }

        speakers.add(speaker)

        if (speakers.size == 1) {
            Log.d(TAG, "onSpeakerFound found main speaker MAC = ${speaker.mac}")
            mainSpeaker = speaker
            selectedSpeaker = speaker
            FragmentController.model = mainSpeaker
            BluetoothScanner.stopScan()
        }

        FragmentController.update()
    }

    fun onSpeakerConnected(speaker: SpeakerModel) {
        Log.d(TAG, "onSpeakerConnected")

        if (speaker == mainSpeaker) {
            UIHelper.openMainActivity()
        }

        BluetoothProtocol.requestSpeakerInfo(speaker)
    }

    fun getSpeaker(index: Int): SpeakerModel {
        return speakers[index]
    }
}
