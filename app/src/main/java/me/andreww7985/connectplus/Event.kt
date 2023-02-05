package me.andreww7985.connectplus

import timber.log.Timber

class Event {
    private var listeners = HashSet<() -> Unit>()

    fun subscribe(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: () -> Unit) {
        listeners.remove(listener)
    }

    fun fire() {
        Timber.d("Firing event $this, listeners: $listeners")
        listeners.forEach { it.invoke() }
    }
}