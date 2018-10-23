package me.andreww7985.connectplus.discovery

import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter

class DiscoveryPresenter : BasePresenter(SpeakerManager) {
    override fun onViewAttached() {
        val view = view as DiscoveryView
        val model = model as SpeakerManager

        model.speakerFoundEvent.subscribe {
            view.showConnecting(model.selectedSpeaker!!.bleConnection.getBluetoothName())
        }

        val speaker = model.selectedSpeaker
        if (speaker != null) {
            view.showConnecting(speaker.bleConnection.getBluetoothName())
        }
    }

    override fun destroy() {
        (model as SpeakerManager).speakerFoundEvent.unsubscribe()
    }
}