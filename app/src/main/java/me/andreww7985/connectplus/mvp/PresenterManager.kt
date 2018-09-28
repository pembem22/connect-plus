package me.andreww7985.connectplus.mvp

object PresenterManager {
    val presenters = HashMap<Class<out BasePresenter>, BasePresenter>()

    fun getPresenter(presenterClass : Class<out BasePresenter>) : BasePresenter {
        var presenter = presenters[presenterClass]

        if (presenter == null) {
            presenter = presenterClass.newInstance()!!
        }

        return presenter
    }

    fun destroyPresenter(presenterClass : Class<out BasePresenter>) {
        presenters.remove(presenterClass)
    }
}