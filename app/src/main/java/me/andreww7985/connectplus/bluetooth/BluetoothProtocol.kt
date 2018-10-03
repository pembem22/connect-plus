package me.andreww7985.connectplus.bluetooth

import android.bluetooth.le.ScanResult
import android.util.Log
import android.util.SparseArray
import me.andreww7985.connectplus.feature.Feature
import me.andreww7985.connectplus.feature.model.BatteryNameFeatureModel
import me.andreww7985.connectplus.feature.model.FeedbackSoundsFeatureModel
import me.andreww7985.connectplus.feature.model.FirmwareVersionFeatureModel
import me.andreww7985.connectplus.feature.model.SpeakerphoneModeFeatureModel
import me.andreww7985.connectplus.hardware.HardwareColor
import me.andreww7985.connectplus.hardware.HardwareModel
import me.andreww7985.connectplus.helpers.HexHelper
import me.andreww7985.connectplus.speaker.SpeakerManager
import me.andreww7985.connectplus.speaker.SpeakerModel
import me.andreww7985.connectplus.task.ConnectToSpeakerTask
import me.andreww7985.connectplus.task.SpeakerTaskExecutor


val Int.byte get() = this.toByte()

object BluetoothProtocol {
    private const val TAG = "BluetoothProtocol"

    fun connect(scanResult: ScanResult) {
        val scanRecord = scanResult.scanRecord

        if (scanRecord == null) {
            Log.w(TAG, "connect scanRecord = null")
            return
        }

        val scanRecordBytes = scanRecord.bytes
        Log.d(TAG, "connect scanRecordBytes = ${HexHelper.bytesToHex(scanRecordBytes)}")

        val manufacturerData = SparseArray<ByteArray>()
        var currentPos = 0
        while (currentPos < scanRecordBytes.size) {
            val currentPos2 = currentPos + 1
            val length = scanRecordBytes[currentPos].toInt() and 255
            if (length == 0) {
                break
            }
            val dataLength = length - 1
            currentPos = currentPos2 + 1

            when (scanRecordBytes[currentPos2]) {
                0xFF.byte -> manufacturerData.put((scanRecordBytes[currentPos + 1].toInt() and 255 shl 8) + (scanRecordBytes[currentPos].toInt() and 255),
                        scanRecordBytes.copyOfRange(currentPos + 2, currentPos + 2 + dataLength))
            }

            currentPos += dataLength
        }

        val speakerData = manufacturerData[87]

        if (speakerData == null) {
            Log.w(TAG, "connect speakerData = null")
            return
        }

        Log.d(TAG, "connect parsed = ${HexHelper.bytesToHex(speakerData)}")
        val speakerModel = HardwareModel.from(HexHelper.bytesToHex(byteArrayOf(speakerData[1], speakerData[0])))
        val speakerColor = HardwareColor.from(speakerModel, speakerData[2])
        //val speakerRole = speakerData[3]

        val speaker = SpeakerModel(speakerModel, speakerColor, scanResult.device, HexHelper.bytesToHex(speakerData))

        if (SpeakerManager.speakers.size == 0 /* current task != wait until speaker reboot */) {
            SpeakerTaskExecutor.startTask(ConnectToSpeakerTask(speaker))
        }

        SpeakerManager.onSpeakerFound(speaker)
    }

    fun requestSpeakerInfo(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_SPEAKER_INFO, byteArrayOf(0)))
    }

    fun requestSpeakerFirmware(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_FIRMWARE_VERSION))
    }

    fun setActiveChannel(speaker: SpeakerModel, channel: ActiveChannel) {
        setSpeakerData(speaker, DataToken.TOKEN_ACTIVE_CHANNEL, byteArrayOf(channel.value))
    }

    fun requestAudioFeedback(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_AUDIO_FEEDBACK))
    }

    fun setSpeakerphoneMode(speaker: SpeakerModel, speakerphoneMode: Boolean) {
        sendPacket(makePacket(PacketType.SET_SPEAKERPHONE_MODE, byteArrayOf(if (speakerphoneMode) 1 else 0)))
    }

    fun requestSpeakerphoneMode(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.REQ_SPEAKERPHONE_MODE))
    }

    fun setAudioFeedback(speaker: SpeakerModel, audioFeedback: Boolean) {
        sendPacket(makePacket(PacketType.SET_AUDIO_FEEDBACK, byteArrayOf(if (audioFeedback) 1 else 0)))
    }

    fun renameSpeaker(speaker: SpeakerModel, name: String) {
        val bytes = name.toByteArray()
        setSpeakerData(speaker, DataToken.TOKEN_NAME, byteArrayOf(bytes.size.byte, *bytes))
    }

    fun playSound(speaker: SpeakerModel) {
        sendPacket(makePacket(PacketType.PLAY_SOUND, byteArrayOf(speaker.index.byte)))
    }

    fun setSpeakerData(speaker: SpeakerModel, token: DataToken, bytes: ByteArray) {
        sendPacket(makePacket(PacketType.SET_SPEAKER_INFO, byteArrayOf(speaker.index.byte, token.value, *bytes)))
    }

    fun sendPacket(bytes: ByteArray) {
        Log.d(TAG, "sendPacket ${HexHelper.bytesToHex(bytes)}")
        SpeakerManager.mainSpeaker!!.sendPacket(bytes)
    }

    fun makePacket(packetType: PacketType, bytes: ByteArray = byteArrayOf()): ByteArray {
        Log.d(TAG, "makePacket $packetType ${HexHelper.bytesToHex(bytes)}")

        if (bytes.size > 255) {
            throw IllegalArgumentException("too long p")
        }

        return byteArrayOf(0xAA.byte, packetType.id.byte, bytes.size.byte, *bytes)
    }

    fun onPacket(speaker: SpeakerModel, bytes: ByteArray) {
        Log.d(TAG, "onPacket ${HexHelper.bytesToHex(bytes)}")

        if (bytes[0] != 0xAA.byte) {
            throw IllegalArgumentException("Bad packet header")
        }

        val length = bytes[2].toInt() and 0xFF
        val payload = ByteArray(length)
        System.arraycopy(bytes, 3, payload, 0, Math.min(length, bytes.size - 3))

        when (PacketType.from(bytes[1])) {
            PacketType.ACK -> {
                Log.d(TAG, "Ack!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!}")
            }
            PacketType.RES_SPEAKER_INFO -> {
                speaker.index = payload[0].toInt() and 0xFF

                var name: String? = null
                var batteryCharging: Boolean? = null
                var batteryLevel: Int? = null

                var pointer = 1
                while (pointer < payload.size) {
                    when (DataToken.from(payload[pointer])) {
                        DataToken.TOKEN_MODEL -> {
                            pointer += 3
                        }
                        DataToken.TOKEN_COLOR -> {
                            pointer += 2
                        }
                        DataToken.TOKEN_BATTERY_STATUS -> {
                            val batteryStatus = payload[pointer + 1].toInt() and 0xFF
                            pointer += 2

                            batteryCharging = batteryStatus > 100
                            batteryLevel = batteryStatus % 128
                        }
                        DataToken.TOKEN_LINKED_DEVICE_COUNT -> {
                            val likedDeviceCount = payload[pointer + 1].toInt() and 0xFF
                            pointer += 2
                        }
                        DataToken.TOKEN_ACTIVE_CHANNEL -> {
                            val activeChannel = ActiveChannel.from(payload[pointer + 1])!!
                            // speaker.activeChannel = activeChannel
                            pointer += 2

                        }
                        DataToken.TOKEN_AUDIO_SOURCE -> {
                            pointer += 2
                        }
                        DataToken.TOKEN_MAC -> {
                            pointer += 8
                        }
                        DataToken.TOKEN_NAME -> {
                            val nameBytesLength = payload[pointer + 1].toInt() and 0xFF
                            val nameBytes = ByteArray(nameBytesLength)
                            for (i in 0 until nameBytesLength) nameBytes[i] = payload[pointer + 2 + i]
                            pointer += 2 + nameBytesLength

                            name = String(nameBytes)

                        }
                        else -> {
                            Log.d(TAG, "onPacket RES_SPEAKER_INFO unknown token")
                            pointer++
                        }
                    }
                }

                if (name != null && batteryLevel != null && batteryCharging != null) {
                    val feature = speaker.getOrCreateFeatureModel(Feature.BATTERY_NAME) as BatteryNameFeatureModel

                    feature.setData(batteryCharging, batteryLevel, name)
                }

                requestSpeakerFirmware(speaker)
            }
            PacketType.RES_FIRMWARE_VERSION -> {
                val feature = speaker.getOrCreateFeatureModel(Feature.FIRMWARE_VERSION) as FirmwareVersionFeatureModel

                val firmwareMajor = payload[0].toInt() and 0xFF
                val firmwareMinor = payload[1].toInt() and 0xFF
                val firmwareBuild = if (payload.size >= 3) payload[2].toInt() and 0xFF else -1
                feature.setData(firmwareMajor, firmwareMinor, firmwareBuild)

                requestAudioFeedback(speaker)
            }
            PacketType.RES_AUDIO_FEEDBACK -> {
                val feature = speaker.getOrCreateFeatureModel(Feature.FEEDBACK_SOUNDS) as FeedbackSoundsFeatureModel

                val feedbackSounds = payload[0] == 1.byte
                feature.setData(feedbackSounds)

                requestSpeakerphoneMode(speaker)
            }
            PacketType.RES_SPEAKERPHONE_MODE -> {
                val feature = speaker.getOrCreateFeatureModel(Feature.SPEAKERPHONE_MODE) as SpeakerphoneModeFeatureModel

                val speakerphoneMode = payload[0] == 1.byte
                feature.setData(speakerphoneMode)
            }
            PacketType.RES_DFU_STATUS_CHANGE -> {
                /* val speaker = SpeakerManager.mainSpeaker!!
                val dfu = speaker.dfu
                val status = if (payload.size == 1) payload[0].toInt() else payload[1].toInt()

                when (status) {
                    0 -> {
                        ConnectPlusApp.logFirebaseEvent("dfu_error")
                        speaker.dfuState = DFUState.FLASHING_ERROR
                    }
                    1, 2 -> {
                        Log.d(TAG, "onPacket RES_DFU_STATUS_CHANGE sending ${dfu.currentChunk} chunk")

                        if (status == 1) {
                            speaker.dfuState = DFUState.FLASHING
                        }

                        sendPacket(makePacket(PacketType.SET_DFU_DATA, dfu.getNextChunk()))
                        FragmentController.update()
                    }
                    3 -> {
                        ConnectPlusApp.logFirebaseEvent("dfu_done")
                        speaker.dfuState = DFUState.FLASHING_DONE
                    }
                }*/
            }
        }
    }
}