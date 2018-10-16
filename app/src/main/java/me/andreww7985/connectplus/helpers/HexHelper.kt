package me.andreww7985.connectplus.helpers

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

object HexHelper {
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

    fun bytesToHex(bytes: ByteArray): String {
        val result = StringBuffer()

        for (b in bytes) {
            result.append(String.format("%02X", b))
        }

        return result.toString()
    }
}