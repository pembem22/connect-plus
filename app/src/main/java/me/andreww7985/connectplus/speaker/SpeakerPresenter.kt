package me.andreww7985.connectplus.speaker

import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.feature.Feature
import me.andreww7985.connectplus.feature.model.BaseFeatureModel
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.mvp.BasePresenter

class SpeakerPresenter : BasePresenter(SpeakerManager.selectedSpeaker!!) {
    init {
        (model as SpeakerModel).setOnFeatureAddedListener { feature, featureModel ->
            addFeatureToView(feature, featureModel)
        }
    }

    fun addFeatureToView(feature: Feature, featureModel: BaseFeatureModel) {
        val view = view as SpeakerView? ?: return

        val featurePresenter = Feature.Factory.makeFeaturePesenter(feature, featureModel)
        val featureView = Feature.Factory.makeFeatureView(feature, view.context!!, featurePresenter)

        view.showFeatureView(featureView)
    }

    override fun onViewAttached() {
        val view = view as SpeakerView? ?: return
        val model = model as SpeakerModel

        view.setColor(model.color.name)
        view.setModel(model.model.name)
        view.setData(model.scanRecord)
        view.setMac(model.mac)

        UIHelper.getLogoImage(model)?.let {
            view.setLogoImage(it)
        }

        UIHelper.getSpeakerImage(model)?.let {
            view.setSpeakerImage(it)
        }

        model.featureModules.forEach { (feature, featureModel) ->
            addFeatureToView(feature, featureModel)
        }
    }

    fun onPlaySoundPressed() {
        BluetoothProtocol.playSound(model as SpeakerModel)
    }
}