package me.andreww7985.connectplus.mvp

abstract class BasePresenter<ViewClass : BaseView, ModelClass : BaseModel>(val model: ModelClass) {
    var view: ViewClass? = null

    fun attachView(view: ViewClass) {
        this.view = view
        onViewAttached()
    }

    fun detachView() {
        view = null
    }

    open fun destroy() {}

    abstract fun onViewAttached()
}