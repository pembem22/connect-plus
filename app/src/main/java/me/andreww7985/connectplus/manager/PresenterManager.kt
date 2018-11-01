package me.andreww7985.connectplus.manager

import me.andreww7985.connectplus.mvp.BasePresenter
import timber.log.Timber

object PresenterManager {
    private val presenters = HashMap<Class<out BasePresenter>, BasePresenter>()

    fun getPresenter(presenterClass: Class<out BasePresenter>): BasePresenter {
        var presenter = presenters[presenterClass]

        if (presenter == null) {
            presenter = presenterClass.newInstance()!!
            presenters[presenterClass] = presenter
        }

        return presenter
    }

    fun destroyPresenter(presenterClass: Class<out BasePresenter>) {
        val presenter = presenters[presenterClass]
        if (presenter == null) {
            Timber.w("destroyPresenter nonexistent presenter ${presenterClass.simpleName}")
            return
        }

        presenter.destroy()
        presenters.remove(presenterClass)
    }
}