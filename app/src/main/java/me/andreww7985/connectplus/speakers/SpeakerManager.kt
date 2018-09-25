package me.andreww7985.connectplus.speakers

import android.util.Log
import me.andreww7985.connectplus.ConnectPlusApp
import me.andreww7985.connectplus.bluetooth.BluetoothScanner
import me.andreww7985.connectplus.controller.FragmentController
import me.andreww7985.connectplus.helpers.UIHelper

object SpeakerManager {
    private const val TAG = "SpeakerManager"
    var mainSpeaker: Speaker? = null
    //val connectPlusEnabled = false
    val speakers = ArrayList<Speaker>()
    val leftSpeakers = ArrayList<Speaker>()
    val rightSpeakers = ArrayList<Speaker>()
    val stereoSpeakers = ArrayList<Speaker>()

    fun onSpeakerFound(speaker: Speaker) {
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
            FragmentController.model = mainSpeaker
            BluetoothScanner.stopScan()
        }

        FragmentController.update()
    }

    fun onSpeakerConnected(speaker: Speaker) {
        Log.d(TAG, "onSpeakerConnected")

        if (speaker == mainSpeaker) {
            UIHelper.openMainActivity()
        }
    }

    fun getSpeaker(index: Int): Speaker {
        return speakers[index]
    }
}
