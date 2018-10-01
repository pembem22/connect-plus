package me.andreww7985.connectplus.feature.model

import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.speaker.SpeakerModel

abstract class BaseFeatureModel(val speaker: SpeakerModel) : BaseModel {
    private var onDataChangedCallback: (() -> Unit)? = null
    private var isDataInitialized = false

    fun setDataChangedListener(callback: () -> Unit) {
        onDataChangedCallback = callback

        if (isDataInitialized) dataChanged()
    }

    fun dataChanged() {
        isDataInitialized = true
        onDataChangedCallback?.invoke()
    }
}

