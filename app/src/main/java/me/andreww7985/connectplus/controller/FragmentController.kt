package me.andreww7985.connectplus.controller

import android.util.Log
import me.andreww7985.connectplus.speakers.Speaker
import me.andreww7985.connectplus.ui.IUpdatableFragment

object FragmentController {
    const val TAG = "FragmentController"
    var model: Speaker? = null
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