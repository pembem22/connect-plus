package me.andreww7985.connectplus.feature.view

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.feature_speakerphone_mode.view.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.feature.presenter.SpeakerphoneModeFeaturePresenter

@SuppressLint("ViewConstructor")
class SpeakerphoneModeFeatureView(context: Context, private val presenter: SpeakerphoneModeFeaturePresenter) : BaseFeatureView(context, presenter) {
    init {
        addView(inflate(context, R.layout.feature_speakerphone_mode, null))

        dashboard_speakerphone_mode_value.setOnCheckedChangeListener { _, isChecked -> presenter.onSpeakerphoneModeChanged(isChecked) }

        presenter.attachView(this)
    }

    fun setSpeakerphoneModeValue(value: Boolean) {
        dashboard_speakerphone_mode_value.isChecked = value
    }
}