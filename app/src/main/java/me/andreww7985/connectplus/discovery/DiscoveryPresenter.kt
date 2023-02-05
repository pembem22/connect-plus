package me.andreww7985.connectplus.discovery

import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter

class DiscoveryPresenter : BasePresenter<DiscoveryView, SpeakerManager>(SpeakerManager) {
    private val speakerFoundEventListener = { view?.showConnecting(model.selectedSpeaker!!.bleConnection.getBluetoothName()); Unit }

    override fun onViewAttached() {
        model.speakerFoundEvent.subscribe(speakerFoundEventListener)

        val speaker = model.selectedSpeaker
        if (speaker != null) {
            view!!.showConnecting(speaker.bleConnection.getBluetoothName())
        }
    }

    override fun destroy() {
        model.speakerFoundEvent.unsubscribe(speakerFoundEventListener)
    }
}