package me.andreww7985.connectplus.manager

import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.mvp.BasePresenter
import me.andreww7985.connectplus.mvp.BaseView
import timber.log.Timber

object PresenterManager {
    private val presenters = HashMap<Class<out BasePresenter<out BaseView, out BaseModel>>, BasePresenter<out BaseView, out BaseModel>>()

    fun getPresenter(presenterClass: Class<out BasePresenter<out BaseView, out BaseModel>>) =
            presenters[presenterClass] ?: run {
                val presenter = presenterClass.newInstance()
                presenters[presenterClass] = presenter
                presenter
            }

    fun destroyPresenter(presenterClass: Class<out BasePresenter<out BaseView, out BaseModel>>) {
        val presenter = presenters[presenterClass]
        if (presenter == null) {
            Timber.w("destroyPresenter non-existent presenter ${presenterClass.simpleName}")
            return
        }

        presenter.destroy()
        presenters.remove(presenterClass)
    }
}