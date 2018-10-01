package me.andreww7985.connectplus.feature.presenter

import me.andreww7985.connectplus.feature.model.FirmwareVersionFeatureModel
import me.andreww7985.connectplus.feature.view.FirmwareVersionFeatureView

class FirmwareVersionFeaturePresenter(model: FirmwareVersionFeatureModel) : BaseFeaturePresenter(model) {
    override fun onViewAttached() {
        val model = getModel()
        val view = getView()

        model.setDataChangedListener {
            var value = "${model.firmwareMajor}.${model.firmwareMinor}"
            if (model.firmwareBuild != -1) value += ".${model.firmwareBuild}"

            view.setFirmwareVersionValue(value)
        }
    }

    private fun getView() = view!! as FirmwareVersionFeatureView
    private fun getModel() = model as FirmwareVersionFeatureModel
}