package me.andreww7985.connectplus.feature.presenter

import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.feature.model.FeedbackSoundsFeatureModel
import me.andreww7985.connectplus.feature.view.FeedbackSoundsFeatureView

class FeedbackSoundsFeaturePresenter(model: FeedbackSoundsFeatureModel) : BaseFeaturePresenter(model) {
    override fun onViewAttached() {
        val model = getModel()
        val view = getView()

        model.setDataChangedListener {
            view.setFeedbackSoundsEnabled(model.feedbackSoundsEnabled)
        }
    }

    fun onFeedbackSoundsChanged(value: Boolean) {
        BluetoothProtocol.setAudioFeedback(getModel().speaker, value)
    }

    private fun getView() = view!! as FeedbackSoundsFeatureView
    private fun getModel() = model as FeedbackSoundsFeatureModel
}