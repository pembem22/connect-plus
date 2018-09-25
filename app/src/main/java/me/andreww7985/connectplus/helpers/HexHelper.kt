package me.andreww7985.connectplus.helpers

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

object HexHelper {
    private const val TAG = "HexHelper"

    fun intToBytes(int: Int): ByteArray {
        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        dos.writeInt(int)
        dos.flush()
        dos.close()
        val result = baos.toByteArray()
        baos.close()
        return result
    }

    fun hexToBytes(string: String): ByteArray {
        val len = string.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(string[i], 16) shl 4) + Character.digit(string[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun bytesToHex(bytes: ByteArray): String {
        val result = StringBuffer()

        for (b in bytes) {
            result.append(String.format("%02X", b))
        }

        return result.toString()
    }
}