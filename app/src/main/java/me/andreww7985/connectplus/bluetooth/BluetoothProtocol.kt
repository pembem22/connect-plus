package me.andreww7985.connectplus.bluetooth

import android.bluetooth.le.ScanResult
import android.util.Log
import android.util.SparseArray
import me.andreww7985.connectplus.ConnectPlusApp
import me.andreww7985.connectplus.controller.FragmentController
import me.andreww7985.connectplus.dfu.DFUState
import me.andreww7985.connectplus.helpers.HexHelper
import me.andreww7985.connectplus.speakers.Speaker
import me.andreww7985.connectplus.speakers.SpeakerColor
import me.andreww7985.connectplus.speakers.SpeakerManager
import me.andreww7985.connectplus.speakers.SpeakerModel
import me.andreww7985.connectplus.task.ConnectToSpeakerTask
import me.andreww7985.connectplus.task.SpeakerTaskExecutor


val Int.b get() = this.toByte()

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
                0xFF.b -> manufacturerData.put((scanRecordBytes[currentPos + 1].toInt() and 255 shl 8) + (scanRecordBytes[currentPos].toInt() and 255),
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
        val speakerModel = SpeakerModel.from(HexHelper.bytesToHex(byteArrayOf(speakerData[1], speakerData[0])))
        val speakerColor = SpeakerColor.from(speakerModel, speakerData[2])
        //val speakerRole = speakerData[3]
        val speaker = Speaker(speakerModel, speakerColor, scanResult.device, HexHelper.bytesToHex(speakerData))

        if (SpeakerManager.speakers.size == 0 /* current task != wait until speaker reboot */) {
            SpeakerTaskExecutor.startTask(ConnectToSpeakerTask(speaker))
        }

        SpeakerManager.onSpeakerFound(speaker)
    }

    fun requestSpeakerInfo(speaker: Speaker) {
        sendPacket(makePacket(PacketType.REQ_SPEAKER_INFO, byteArrayOf(0)))
    }

    fun requestSpeakerFirmware(speaker: Speaker) {
        sendPacket(makePacket(PacketType.REQ_FIRMWARE_VERSION))
    }

    fun setActiveChannel(speaker: Speaker, channel: ActiveChannel) {
        setSpeakerData(speaker, DataToken.TOKEN_ACTIVE_CHANNEL, byteArrayOf(channel.value))
    }

    fun requestAudioFeedback(speaker: Speaker) {
        sendPacket(makePacket(PacketType.REQ_AUDIO_FEEDBACK))
    }

    fun setAudioFeedback(speaker: Speaker, audioFeedback: Boolean) {
        sendPacket(makePacket(PacketType.SET_AUDIO_FEEDBACK, byteArrayOf(if (audioFeedback) 1 else 0)))
    }

    fun renameSpeaker(speaker: Speaker, name: String) {
        val bytes = name.toByteArray()
        setSpeakerData(speaker, DataToken.TOKEN_NAME, byteArrayOf(bytes.size.b, *bytes))
    }

    fun playSound(speaker: Speaker) {
        sendPacket(makePacket(PacketType.PLAY_SOUND, byteArrayOf(speaker.index!!.b)))
    }

    fun setSpeakerData(speaker: Speaker, token: DataToken, bytes: ByteArray) {
        sendPacket(makePacket(PacketType.SET_SPEAKER_INFO, byteArrayOf(speaker.index!!.b, token.value, *bytes)))
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

        return byteArrayOf(0xAA.toByte(), packetType.value, bytes.size.toByte(), *bytes)
    }

    fun onPacket(bytes: ByteArray) {
        Log.d(TAG, "onPacket ${HexHelper.bytesToHex(bytes)}")

        if (bytes[0] != 0xAA.b) {
            throw IllegalArgumentException("Bad packet header")
        }

        val length = bytes[2].toInt() and 0xFF
        val payload = ByteArray(length)
        System.arraycopy(bytes, 3, payload, 0, Math.min(length, bytes.size - 3))
        when (PacketType.from(bytes[1])) {
            PacketType.ACK -> {
            }
            PacketType.RES_SPEAKER_INFO -> {
                val speaker = FragmentController.model!!
                speaker.index = payload[0].toInt() and 0xFF

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
                            speaker.batteryCharging = batteryStatus > 100
                            speaker.batteryLevel = batteryStatus % 128
                            pointer += 2
                        }
                        DataToken.TOKEN_LINKED_DEVICE_COUNT -> {
                            val likedDeviceCount = payload[pointer + 1].toInt() and 0xFF
                            pointer += 2
                        }
                        DataToken.TOKEN_ACTIVE_CHANNEL -> {
                            val activeChannel = ActiveChannel.from(payload[pointer + 1])!!
                            speaker.activeChannel = activeChannel
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
                            val name = String(nameBytes)
                            speaker.name = name
                            pointer += 2 + nameBytesLength
                        }
                        else -> {
                            Log.d(TAG, "onPacket RES_SPEAKER_INFO unknown token")
                            pointer++
                        }
                    }
                }

                speaker.firmware ?: requestSpeakerFirmware(speaker)
            }
            PacketType.RES_FIRMWARE_VERSION -> {
                val speaker = FragmentController.model!!
                speaker.firmware = "${payload[0].toInt() and 0xFF}.${payload[1].toInt() and 0xFF}.${payload[2].toInt() and 0xFF}"

                ConnectPlusApp.logSpeakerEvent("speaker_firmware") {
                    putString("speaker_firmware", speaker.firmware)
                }

                speaker.audioFeedback ?: requestAudioFeedback(speaker)
            }
            PacketType.RES_AUDIO_FEEDBACK -> {
                if (payload.isEmpty()) {
                    Log.w(TAG, "onPacket RES_AUDIO_FEEDBACK empty payload")
                    return
                }
                FragmentController.model!!.audioFeedback = payload[0].toInt() == 1
            }
            PacketType.RES_DFU_STATUS_CHANGE -> {
                val speaker = SpeakerManager.mainSpeaker!!
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
                }
            }
        }
    }
}