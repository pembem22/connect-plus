package me.andreww7985.connectplus

class Event {
    var listener: (Event.() -> Unit)? = null

    fun subscribe(listener: Event.() -> Unit) {
        this.listener = listener
    }

    fun unsubscribe() {
        listener = null
    }

    fun fire() {
        listener?.invoke(this)
    }
}