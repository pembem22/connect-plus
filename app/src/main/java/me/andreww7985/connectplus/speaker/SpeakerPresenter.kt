package me.andreww7985.connectplus.speaker

import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BasePresenter
import me.andreww7985.connectplus.protocol.DataToken
import me.andreww7985.connectplus.protocol.Packet
import me.andreww7985.connectplus.protocol.PacketType
import me.andreww7985.connectplus.speaker.Feature.Type.*

class SpeakerPresenter : BasePresenter(SpeakerManager.selectedSpeaker!!) {
    override fun onViewAttached() {
        val view = view as SpeakerView
        val model = model as SpeakerModel

        view.setDeveloperData(model.mac, model.scanRecord, model.color.name, model.model.name)

        val app = App.instance
        val resources = app.resources
        val packageName = app.packageName

        val logoDrawableId = resources.getIdentifier(String.format("logo_%s", model.model.name.toLowerCase()), "drawable", packageName)
        val speakerDrawableId = resources.getIdentifier(String.format("img_%s_%s", model.model.name.toLowerCase(), model.color.name.toLowerCase()), "drawable", packageName)

        view.setSpeakerImages(logoDrawableId, speakerDrawableId)

        model.featuresUpdatedEvent.subscribe {
            updateFeatures()
        }

        updateFeatures()
    }

    private fun updateFeatures() {
        val view = view as SpeakerView? ?: return
        val model = model as SpeakerModel

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
            }
        }
    }

    fun onRenamePressed() {
        val feature = (model as SpeakerModel).getFeature(Feature.Type.BATTERY_NAME) as Feature.BatteryName

        (view as SpeakerView).showRenameAlertDialog(feature.deviceName!!)
    }

    fun onRenameDialogConfirmed(newName: String) {
        val model = model as SpeakerModel
        val feature = model.getFeature(Feature.Type.BATTERY_NAME) as Feature.BatteryName

        feature.deviceName = newName

        val bytes = newName.toByteArray()
        model.sendPacket(
                Packet(PacketType.SET_SPEAKER_INFO,
                        byteArrayOf(
                                model.index.toByte(),
                                DataToken.TOKEN_NAME.id.toByte(),
                                bytes.size.toByte(),
                                *bytes)))
    }

    fun onPlaySoundPressed() {
        val view = view as SpeakerView
        val model = model as SpeakerModel

        val audioFeedback = (model.getFeature(Feature.Type.FEEDBACK_SOUNDS) as Feature.FeedbackSounds?)?.enabled
                ?: true
        if (audioFeedback)
            model.sendPacket(Packet(PacketType.PLAY_SOUND))
        else
            view.showFeedbackSoundsDisabledMessage()
    }

    fun onSpeakerphoneModeChanged(value: Boolean) {
        val model = model as SpeakerModel

        val speakerphoneMode = model.getFeature(Feature.Type.SPEAKERPHONE_MODE) as Feature.SpeakerphoneMode

        BluetoothProtocol.setSpeakerphoneMode(model, value)
        speakerphoneMode.enabled = value
        model.featuresChanged()
    }

    fun onFeedbackSoundsChanged(value: Boolean) {
        val model = model as SpeakerModel

        val feedbackSounds = model.getFeature(Feature.Type.FEEDBACK_SOUNDS) as Feature.FeedbackSounds

        BluetoothProtocol.setAudioFeedback(model, value)
        feedbackSounds.enabled = value
        model.featuresChanged()
    }

    override fun destroy() {
        (model as SpeakerModel).featuresUpdatedEvent.unsubscribe()
    }
}