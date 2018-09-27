package me.andreww7985.connectplus.feature.view

import android.view.View

abstract class BaseFeatureView {
    var view: View? = null

    abstract fun initView()
    fun destroyView() {
        view = null
    }
}