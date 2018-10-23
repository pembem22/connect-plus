package me.andreww7985.connectplus.mvp

abstract class BasePresenter(val model: BaseModel) {
    var view: BaseView? = null

    fun attachView(view: BaseView) {
        this.view = view
        onViewAttached()
    }

    fun detachView() {
        view = null
    }

    open fun destroy() {}

    abstract fun onViewAttached()
}