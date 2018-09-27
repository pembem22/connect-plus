package me.andreww7985.connectplus.feature.presenter

import me.andreww7985.connectplus.feature.model.BatteryNameFeatureModel
import me.andreww7985.connectplus.feature.view.BaseFeatureView

abstract class BaseFeaturePresenter (val model: BatteryNameFeatureModel) {
    var view: BaseFeatureView? = null

    fun attachView(view: BaseFeatureView) {
        this.view = view
    }

    abstract fun onViewReady()
}