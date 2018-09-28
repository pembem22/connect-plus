package me.andreww7985.connectplus.controller

import android.util.Log
import me.andreww7985.connectplus.speaker.SpeakerModel
import me.andreww7985.connectplus.ui.IUpdatableFragment

object FragmentController {
    const val TAG = "FragmentController"
    var model: SpeakerModel? = null
    var view: IUpdatableFragment? = null
        set(value) {
            field = value
            Log.d(TAG, "view set to $value")
            update()
        }

    fun update() {
        view?.update()
    }
}