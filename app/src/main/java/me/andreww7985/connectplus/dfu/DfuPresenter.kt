package me.andreww7985.connectplus.dfu

import android.net.Uri
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter
import me.andreww7985.connectplus.speaker.Feature

class DfuPresenter : BasePresenter(SpeakerManager.selectedSpeaker!!.dfuModel) {
    override fun onViewAttached() {
        val model = model as DfuModel
        val view = view as DfuView

        model.speaker.featuresUpdatedEvent.subscribe {
            updateView()
        }

        model.fileLoadingErrorEvent.subscribe {
            view.showFileBrowserError()
        }

        model.modelChangedEvent.subscribe {
            updateView()
        }

        updateView()
    }

    override fun destroy() {
        val model = model as DfuModel
        model.speaker.featuresUpdatedEvent.unsubscribe()
        model.fileLoadingErrorEvent.unsubscribe()
        model.modelChangedEvent.unsubscribe()
    }

    fun isSpeakerCharging() =
            ((model as DfuModel).speaker.getFeature(Feature.Type.BATTERY_NAME) as Feature.BatteryName).batteryCharging
                    ?: false


    fun updateView() {
        val model = model as DfuModel
        val view = view as DfuView

        view.updateUi(model, isSpeakerCharging())
    }

    fun cancelDfu() {
        (model as DfuModel).cancelDfu()
    }

    fun startDfu() {
        (model as DfuModel).requestDfu()
    }

    fun loadFile(file: Uri) {
        val model = model as DfuModel
        model.loadFile(file)
    }
}