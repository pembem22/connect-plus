package me.andreww7985.connectplus.dfu

import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.Event
import me.andreww7985.connectplus.helpers.ChecksumHelper
import me.andreww7985.connectplus.helpers.HexHelper
import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.protocol.Packet
import me.andreww7985.connectplus.protocol.PacketType
import me.andreww7985.connectplus.protocol.Protocol4
import me.andreww7985.connectplus.speaker.SpeakerModel
import me.andreww7985.connectplus.speaker.hardware.HwPlatform
import kotlin.math.min

class DfuModel(val speaker: SpeakerModel) : BaseModel {
    companion object {
        const val CHUNK_SIZE = 104
    }

    var filename: String? = null
    var checksum: ByteArray? = null
    var fileBytes: ByteArray? = null
    var fileSize = 0
    var currentChunk = 0

    var state = State.FILE_NOT_SELECTED
    var status = Status.ERROR

    val modelChangedEvent = Event()
    val fileLoadingErrorEvent = Event()
    val wrongFileEvent = Event()

    private fun getFilename(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = App.instance.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }

        if (result == null) {
            result = uri.path
            result?.let {
                val cut = result?.lastIndexOf('/')
                if (cut != null && cut != -1) {
                    result = result?.substring(cut + 1)
                }
            }
        }

        return result
    }

    suspend fun loadFile(file: Uri) {
        val dfuModel = this

        coroutineScope {
            val filename = getFilename(file)

            if (filename == null) {
                fileLoadingErrorEvent.fire()
                return@coroutineScope
            }

            if (!(filename.lowercase().endsWith(".dfu") || filename.lowercase().endsWith(".bin"))) {
                wrongFileEvent.fire()
                return@coroutineScope
            }

            state = State.LOADING_FILE
            dfuModel.filename = filename
            modelChangedEvent.fire()

            launch(Dispatchers.IO) {
                val inputStream = App.instance.contentResolver.openInputStream(file)!!
                val size = inputStream.available()
                val bytes = ByteArray(size)
                inputStream.read(bytes)
                inputStream.close()

                dfuModel.fileBytes = bytes
                dfuModel.fileSize = size
            }.join()

            launch(Dispatchers.Default) {
                val checksum = HexHelper.intToBytes(ChecksumHelper.crc(dfuModel.fileBytes!!).toInt())

                dfuModel.checksum = checksum

                state = State.READY
                modelChangedEvent.fire()

                App.analytics.logEvent("dfu_file_loaded")
            }
        }
    }

    fun cancelDfu() {
        state = State.WAITING_REBOOT
        this.status = Status.CANCELED

        if (speaker.hardware.platform == HwPlatform.PROTOCOL4) {
            speaker.sendProtocol4(Protocol4.stopOta())
        } else {
            speaker.sendPacket(Packet(PacketType.REQ_DFU_CANCEL))
        }

        modelChangedEvent.fire()

        App.analytics.logEvent("dfu_cancelled")
    }

    fun sendNextPacket() {
        val chunkSize = min(CHUNK_SIZE, fileSize - CHUNK_SIZE * currentChunk)
        val chunk = ByteArray(chunkSize)
        System.arraycopy(fileBytes!!, CHUNK_SIZE * currentChunk, chunk, 0, chunkSize)

        speaker.sendPacket(Packet(
                PacketType.SET_DFU_DATA,
                chunk
        ))

        currentChunk++

        modelChangedEvent.fire()
    }

    fun dfuStarted() {
        state = State.FLASHING_DFU
        sendNextPacket()

        App.analytics.logEvent("dfu_started")
    }

    fun requestDfu() {
        if (speaker.hardware.platform == HwPlatform.PROTOCOL4) return requestDfuProtocol4()

        state = State.INITIALIZING_DFU

        val checksum = checksum
        val sizeBytes = HexHelper.intToBytes(fileSize)
        currentChunk = 0

        if (checksum == null) return

        speaker.sendPacket(Packet(
                PacketType.REQ_DFU_START,
                byteArrayOf(checksum[2], checksum[3], checksum[0], checksum[1], *sizeBytes)
        ))

        modelChangedEvent.fire()

        App.analytics.logEvent("dfu_requested")
    }

    /* Protocol 4 OTA (Boombox 4, Xtreme 5): ask for OTA params, then the speaker pulls the file
     * chunk by chunk — each notification names the byte offset it wants next. See Protocol4. */
    private var protocol4Progress = 0f
    private var protocol4PreviousOffset = -1L

    private fun requestDfuProtocol4() {
        state = State.INITIALIZING_DFU
        protocol4Progress = 0f
        protocol4PreviousOffset = -1L
        speaker.sendProtocol4(Protocol4.getOtaInfo())
        modelChangedEvent.fire()
        App.analytics.logEvent("dfu_requested")
    }

    fun onProtocol4OtaInfo() {
        speaker.sendProtocol4(Protocol4.startOta(fileSize))
    }

    fun onProtocol4OtaNotification(notification: Protocol4.OtaNotification) {
        if (status == Status.CANCELED) return
        if (notification.isComplete) return dfuFinished(Status.SUCCESS)

        // The speaker rewinding to offset 0 mid-transfer (when it isn't the completion signal) is an error
        if (protocol4PreviousOffset > 0 && notification.offset == 0L) {
            speaker.sendProtocol4(Protocol4.stopOta())
            return dfuFinished(Status.ERROR)
        }

        state = State.FLASHING_DFU
        val bytes = fileBytes ?: return
        val offset = notification.offset.toInt()
        val end = minOf(offset + Protocol4.OTA_CHUNK_SIZE, fileSize)
        speaker.sendProtocol4(Protocol4.sendOtaChunk(offset, bytes.copyOfRange(offset, end)))

        protocol4Progress = notification.percentage / 10000f
        protocol4PreviousOffset = notification.offset
        modelChangedEvent.fire()
    }

    fun dfuFinished(status: Status) {
        state = State.WAITING_REBOOT
        this.status = status

        modelChangedEvent.fire()

        App.analytics.logEvent("dfu_state_finished") {
            putString("status", status.name)
        }
    }

    fun getProgress() = if (speaker.hardware.platform == HwPlatform.PROTOCOL4) protocol4Progress
        else 1f * currentChunk * CHUNK_SIZE / fileSize

    enum class State {
        FILE_NOT_SELECTED,
        LOADING_FILE,
        READY,
        INITIALIZING_DFU,
        FLASHING_DFU,
        WAITING_REBOOT
    }

    enum class Status {
        SUCCESS,
        CANCELED,
        ERROR
    }
}
