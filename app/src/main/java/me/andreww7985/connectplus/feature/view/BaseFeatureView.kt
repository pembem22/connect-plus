package me.andreww7985.connectplus.feature.view

import android.content.Context
import android.widget.FrameLayout
import me.andreww7985.connectplus.feature.presenter.BaseFeaturePresenter
import me.andreww7985.connectplus.mvp.BaseView

abstract class BaseFeatureView(context: Context, private val presenter: BaseFeaturePresenter) : BaseView, FrameLayout(context) {
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        presenter.detachView()
    }
}