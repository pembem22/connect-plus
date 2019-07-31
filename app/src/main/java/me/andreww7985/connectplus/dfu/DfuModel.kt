package me.andreww7985.connectplus.dfu

import android.net.Uri
import android.provider.OpenableColumns
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.Event
import me.andreww7985.connectplus.helpers.ChecksumHelper
import me.andreww7985.connectplus.helpers.HexHelper
import me.andreww7985.connectplus.mvp.BaseModel
import me.andreww7985.connectplus.protocol.Packet
import me.andreww7985.connectplus.protocol.PacketType
import me.andreww7985.connectplus.speaker.SpeakerModel
import timber.log.Timber

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

    fun loadFile(file: Uri) {
        state = State.LOADING_FILE
        modelChangedEvent.fire()

        Thread {
            try {
                filename = getFilename(file) ?: throw Exception("Can't get DFU file name")

                val inputStream = App.instance.contentResolver.openInputStream(file)!!
                val size = inputStream.available()
                val bytes = ByteArray(size)
                inputStream.read(bytes)
                inputStream.close()
                val checksum = HexHelper.intToBytes(ChecksumHelper.crc(bytes).toInt())

                this.fileBytes = bytes
                this.fileSize = size
                this.checksum = checksum

                state = State.READY
                modelChangedEvent.fire()

                App.analytics.logEvent("dfu_file_loaded")
            } catch (exception: Exception) {
                Timber.e(exception, "loadFile")

                state = State.FILE_NOT_SELECTED
                fileLoadingErrorEvent.fire()
                modelChangedEvent.fire()
            }
        }.start()
    }

    fun cancelDfu() {
        state = State.WAITING_REBOOT
        this.status = Status.CANCELED

        speaker.sendPacket(Packet(
                PacketType.REQ_DFU_CANCEL
        ))

        modelChangedEvent.fire()
    }

    fun sendNextPacket() {
        val chunkSize = Math.min(CHUNK_SIZE, fileSize - CHUNK_SIZE * currentChunk)
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
    }

    fun requestDfu() {
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
    }

    fun dfuFinished(status: Status) {
        state = State.WAITING_REBOOT
        this.status = status

        modelChangedEvent.fire()

        App.analytics.logSpeakerEvent("dfu_state_finished") {
            putString("status", status.name)
        }
    }

    fun getProgress() = 100f * currentChunk * CHUNK_SIZE / fileSize

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