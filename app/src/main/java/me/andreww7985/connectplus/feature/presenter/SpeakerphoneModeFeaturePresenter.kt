package me.andreww7985.connectplus.feature.presenter

import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.feature.model.SpeakerphoneModeFeatureModel
import me.andreww7985.connectplus.feature.view.SpeakerphoneModeFeatureView

class SpeakerphoneModeFeaturePresenter(model: SpeakerphoneModeFeatureModel) : BaseFeaturePresenter(model) {
    override fun onViewAttached() {
        val model = getModel()
        val view = getView()

        model.setDataChangedListener {
            view.setSpeakerphoneModeValue(model.speakerphoneModeEnabled)
        }
    }

    fun onSpeakerphoneModeChanged(value: Boolean) {
        val model = getModel()

        if (value != model.speakerphoneModeEnabled) {
            model.speakerphoneModeEnabled = value
            BluetoothProtocol.setSpeakerphoneMode(getModel().speaker, value)
        }
    }

    private fun getView() = view!! as SpeakerphoneModeFeatureView
    private fun getModel() = model as SpeakerphoneModeFeatureModel
}