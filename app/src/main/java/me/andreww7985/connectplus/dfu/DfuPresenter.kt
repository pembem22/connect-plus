package me.andreww7985.connectplus.dfu

import android.net.Uri
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter
import me.andreww7985.connectplus.speaker.Feature

class DfuPresenter : BasePresenter<DfuView, DfuModel>(SpeakerManager.selectedSpeaker!!.dfuModel) {
    override fun onViewAttached() {
        model.speaker.featuresUpdatedEvent.subscribe {
            updateView()
        }

        model.fileLoadingErrorEvent.subscribe {
            view!!.showFileBrowserError()
        }

        model.modelChangedEvent.subscribe {
            updateView()
        }

        updateView()
    }

    override fun destroy() {
        val model = model
        model.speaker.featuresUpdatedEvent.unsubscribe()
        model.fileLoadingErrorEvent.unsubscribe()
        model.modelChangedEvent.unsubscribe()
    }

    fun isSpeakerCharging() = model.speaker.getFeature<Feature.BatteryName>().batteryCharging
            ?: false


    fun updateView() {
        view!!.updateUi(model, isSpeakerCharging())
    }

    fun cancelDfu() {
        model.cancelDfu()
    }

    fun startDfu() {
        model.requestDfu()
    }

    fun loadFile(file: Uri) {
        model.loadFile(file)
    }
}