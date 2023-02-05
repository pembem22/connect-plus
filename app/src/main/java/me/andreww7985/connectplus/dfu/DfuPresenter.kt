package me.andreww7985.connectplus.dfu

import android.net.Uri
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter
import me.andreww7985.connectplus.speaker.Feature

class DfuPresenter : BasePresenter<DfuView, DfuModel>(SpeakerManager.selectedSpeaker!!.dfuModel) {
    private val featuresUpdatedEventListener = { updateView() }
    private val fileLoadingErrorEventListener = { view?.showFileBrowserError(); Unit }
    private val wrongFileEventListener = { view?.showWrongFile(); Unit }
    private val modelChangedEventListener = { updateView() }

    override fun onViewAttached() {
        model.speaker.featuresUpdatedEvent.subscribe(featuresUpdatedEventListener)
        model.fileLoadingErrorEvent.subscribe(fileLoadingErrorEventListener)
        model.wrongFileEvent.subscribe(wrongFileEventListener)
        model.modelChangedEvent.subscribe(modelChangedEventListener)
        updateView()
    }

    override fun destroy() {
        model.speaker.featuresUpdatedEvent.unsubscribe(featuresUpdatedEventListener)
        model.fileLoadingErrorEvent.unsubscribe(fileLoadingErrorEventListener)
        model.modelChangedEvent.unsubscribe(wrongFileEventListener)
        model.wrongFileEvent.unsubscribe(modelChangedEventListener)
    }

    private fun isSpeakerCharging() =
        model.speaker.getFeature<Feature.BatteryName>().batteryCharging
            ?: false


    private fun updateView() {
        view?.updateUi(model, isSpeakerCharging())
    }

    fun cancelDfu() {
        model.cancelDfu()
    }

    fun startDfu() {
        model.requestDfu()
    }

    suspend fun loadFile(file: Uri) {
        model.loadFile(file)
    }
}