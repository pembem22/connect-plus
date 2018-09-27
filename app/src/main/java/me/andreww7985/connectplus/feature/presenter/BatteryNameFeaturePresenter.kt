package me.andreww7985.connectplus.feature.presenter

import me.andreww7985.connectplus.feature.model.BatteryNameFeatureModel
import me.andreww7985.connectplus.feature.view.BatteryNameFeatureView

class BatteryNameFeaturePresenter(model: BatteryNameFeatureModel) : BaseFeaturePresenter(model) {
    override fun onViewReady() {
        loadModelData(true)
    }

    fun loadModelData(force: Boolean) {
        model.loadData(force) {
            val view = view as BatteryNameFeatureView?
            view ?: return@loadData

            view.setName(model.name)
            view.setBatteryState(model.batteryLevel, model.batteryCharging)
        }
    }
}