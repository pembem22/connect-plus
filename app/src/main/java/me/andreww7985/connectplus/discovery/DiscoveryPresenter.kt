package me.andreww7985.connectplus.discovery

import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter

class DiscoveryPresenter : BasePresenter<DiscoveryView, SpeakerManager>(SpeakerManager) {
    override fun onViewAttached() {
        model.speakerFoundEvent.subscribe {
            view?.showConnecting(model.selectedSpeaker!!.bleConnection.getBluetoothName())
        }

        val speaker = model.selectedSpeaker
        if (speaker != null) {
            view!!.showConnecting(speaker.bleConnection.getBluetoothName())
        }
    }

    override fun destroy() {
        model.speakerFoundEvent.unsubscribe()
    }
}