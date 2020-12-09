package me.andreww7985.connectplus.speaker

import me.andreww7985.connectplus.speaker.hardware.HwColor
import me.andreww7985.connectplus.speaker.hardware.HwConnect
import me.andreww7985.connectplus.speaker.hardware.HwModel
import me.andreww7985.connectplus.speaker.hardware.HwPlatform

data class SpeakerHardware(
        val model: HwModel,
        val color: HwColor,
        val connect: HwConnect,
        val platform: HwPlatform
) {
    fun getMtu() = when (platform) {
        HwPlatform.VIMICRO -> 35
        else -> 517
    }

    companion object {
        fun from(modelId: Int, colorId: Int): SpeakerHardware {
            val model = HwModel.from(modelId)
            val color = HwColor.from(model, colorId)
            val connect = HwConnect.from(model)
            val platform = HwPlatform.from(modelId)
            return SpeakerHardware(model, color, connect, platform)
        }
    }
}

