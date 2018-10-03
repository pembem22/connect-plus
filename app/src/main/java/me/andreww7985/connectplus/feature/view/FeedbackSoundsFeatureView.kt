package me.andreww7985.connectplus.feature.view

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.feature_feedback_sounds.view.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.feature.presenter.FeedbackSoundsFeaturePresenter

@SuppressLint("ViewConstructor")
class FeedbackSoundsFeatureView(context: Context, private val presenter: FeedbackSoundsFeaturePresenter) : BaseFeatureView(context, presenter) {
    init {
        addView(inflate(context, R.layout.feature_feedback_sounds, null))

        dashboard_feedback_sounds_value.setOnCheckedChangeListener { _, isChecked -> presenter.onFeedbackSoundsChanged(isChecked) }

        presenter.attachView(this)
    }

    fun setFeedbackSoundsEnabled(value: Boolean) {
        dashboard_feedback_sounds_value.isChecked = value
    }
}