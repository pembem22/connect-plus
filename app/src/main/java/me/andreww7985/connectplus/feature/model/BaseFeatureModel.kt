package me.andreww7985.connectplus.feature.model

import android.os.Handler
import android.os.Looper
import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.speaker.SpeakerModel

abstract class BaseFeatureModel(val speaker: SpeakerModel) : BaseModel {
    companion object {
        val uiHandler = Handler(Looper.getMainLooper())
    }

    private var onDataChangedCallback: (() -> Unit)? = null
    private var isDataInitialized = false

    fun setDataChangedListener(callback: () -> Unit) {
        onDataChangedCallback = callback

        if (isDataInitialized) dataChanged()
    }

    fun dataChanged() {
        isDataInitialized = true

        uiHandler.post {
            onDataChangedCallback?.invoke()
        }
    }
}

