package me.andreww7985.connectplus.speaker

import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter
import me.andreww7985.connectplus.speaker.Feature.Type.*

class SpeakerPresenter : BasePresenter<SpeakerView, SpeakerModel>(SpeakerManager.selectedSpeaker!!) {
    private val featuresUpdatedEventListener = { updateFeatures() }

    override fun onViewAttached() {
        view!!.setDeveloperData(
            model.mac, model.scanRecord,
            model.hardware.color.name, model.hardware.model.name, model.hardware.platform.name
        )

        val app = App.instance
        val resources = app.resources
        val packageName = app.packageName

        val modelString = model.hardware.model.name.lowercase()
        val colorString = model.hardware.color.name.lowercase()

        val logoDrawableId = resources.getIdentifier(
            "logo_${modelString}", "drawable", packageName
        )
        val speakerDrawableId = resources.getIdentifier(
            "img_${modelString}_${colorString}", "drawable", packageName
        )

        view!!.setSpeakerImages(logoDrawableId, speakerDrawableId)

        model.featuresUpdatedEvent.subscribe(featuresUpdatedEventListener)

        updateFeatures()
    }

    private fun updateFeatures() {
        val view = view ?: return

        view.setIsPlaying(model.isPlaying)

        for ((featureType, feature) in model.features) {
            when (featureType) {
                BATTERY_NAME -> {
                    val batteryName = feature as Feature.BatteryName

                    view.showBatteryNameFeature(batteryName.deviceName!!, batteryName.batteryLevel!!, batteryName.batteryCharging!!)
                }
                FEEDBACK_SOUNDS -> {
                    val feedbackSounds = feature as Feature.FeedbackSounds

                    view.showFeedbackSoundsFeature(feedbackSounds.enabled!!)
                }
                FIRMWARE_VERSION -> {
                    val firmwareVersion = feature as Feature.FirmwareVersion

                    view.showFirmwareVersionFeature(firmwareVersion.minor!!, firmwareVersion.major!!, firmwareVersion.build)
                }
                SPEAKERPHONE_MODE -> {
                    val speakerphoneMode = feature as Feature.SpeakerphoneMode

                    view.showSpeakerphoneModeFeature(speakerphoneMode.enabled!!)
                }
                BASS_LEVEL -> {
                    val bassLevel = feature as Feature.BassLevel

                    view.showBassLevelFeature(bassLevel.level!!)
                }
            }
        }
    }

    fun onRenamePressed() {
        view?.showRenameAlertDialog(model.getFeature<Feature.BatteryName>().deviceName!!)
    }

    fun onRenameDialogConfirmed(name: String) {
        model.getFeature<Feature.BatteryName>().deviceName = name
        model.setName(name)
    }

    fun onPlaySoundPressed() {
        model.requestAnalyticsData()

        val audioFeedback = model.getFeature<Feature.FeedbackSounds>().enabled
                ?: true
        if (audioFeedback) {
            model.playSound()
        } else {
            view?.showFeedbackSoundsDisabledMessage()
        }
    }

    fun onSpeakerphoneModeChanged(value: Boolean) {
        val speakerphoneMode = model.getFeature<Feature.SpeakerphoneMode>()

        if (speakerphoneMode.enabled == value) return

        model.updateSpeakerphoneMode(value)
        speakerphoneMode.enabled = value
        model.featuresChanged()
    }

    fun onFeedbackSoundsChanged(value: Boolean) {
        val feedbackSounds = model.getFeature<Feature.FeedbackSounds>()

        if (feedbackSounds.enabled == value) return

        model.updateAudioFeedback(value)
        feedbackSounds.enabled = value
        model.featuresChanged()
    }

    fun onBassLevelChanged(level: Int) {
        val bassLevel = model.getFeature<Feature.BassLevel>()

        if (bassLevel.level == level) return

        model.updateBassLevel(level)
        bassLevel.level = level
        model.featuresChanged()
    }

    override fun destroy() {
        model.featuresUpdatedEvent.unsubscribe(featuresUpdatedEventListener)
    }
}