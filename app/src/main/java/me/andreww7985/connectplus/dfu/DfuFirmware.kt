package me.andreww7985.connectplus.dfu

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.bluetooth.PacketType
import me.andreww7985.connectplus.helpers.ChecksumHelper
import me.andreww7985.connectplus.helpers.HexHelper
import me.andreww7985.connectplus.helpers.UIHelper

class DfuFirmware(private val file: Uri) {
    private lateinit var bytes: ByteArray
    lateinit var filename: String
    lateinit var checksum: ByteArray
    var size = 0
    var currentChunk = 0

    fun check(context: Context) {
        filename = getFilename(context, file) ?: "Can't get file name"

        try {
            context.contentResolver.openInputStream(file) ?: throw Exception()
        } catch (exception: Exception) {
            UIHelper.showToast("Failed to open file. Use another file manager!", Toast.LENGTH_LONG)
        }

        val inputStream = context.contentResolver.openInputStream(file)!!
        val size = inputStream.available()
        val bytes = ByteArray(size)
        inputStream.read(bytes)
        inputStream.close()

        this.bytes = bytes
        this.size = size
        checksum = HexHelper.intToBytes(ChecksumHelper.crc(bytes).toInt())

        //FragmentController.model!!.dfuState = DfuState.FILE_LOADED
    }

    fun startFlashing() {
        val crcBytes = checksum
        val sizeBytes = HexHelper.intToBytes(size)
        currentChunk = 0

        //FragmentController.model!!.dfuState = DfuState.INITIALIZING_FLASHING
        BluetoothProtocol.sendPacket(BluetoothProtocol.makePacket(PacketType.REQ_DFU_START,
                byteArrayOf(crcBytes[2], crcBytes[3], crcBytes[0], crcBytes[1], *sizeBytes)))
    }

    fun getNextChunk(): ByteArray {
        val size = size
        val chunkSize = Math.min(104, size - 104 * currentChunk)
        val chunk = ByteArray(chunkSize)
        System.arraycopy(bytes, 104 * currentChunk, chunk, 0, chunkSize)

        currentChunk++
        return chunk
    }

    private fun getFilename(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
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
}