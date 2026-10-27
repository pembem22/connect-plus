package me.andreww7985.connectplus.protocol

/**
 * JBL "Protocol 4" (Boombox 4 and newer). Every frame, little-endian:
 * `00 DD | command u16 | packet count u8 | packet index u8 | payload length u16 | payload`.
 * Replies to GET carry a u16 status first, then features as `id u16 | length u16 | value`.
 */
object Protocol4 {
    const val CMD_GET_DEVICE_INFO = 0x0001
    const val CMD_NOTIFICATION_TO_APP = 0x0003
    const val CMD_START_OTA = 0x0101
    const val CMD_SEND_OTA = 0x0102
    const val CMD_STOP_OTA = 0x0103
    const val CMD_OTA_NOTIFICATION = 0x0104

    const val FEATURE_DEVICE_NAME = 0x0006
    const val FEATURE_FIRMWARE_VERSION = 0x000C
    const val FEATURE_BATTERY_STATUS = 0x000D
    const val FEATURE_OTA_MAX_SIZE = 0x0B00
    const val FEATURE_OTA_PACKET_COUNT = 0x0B01
    const val FEATURE_OTA_PACKET_INTERVAL = 0x0003

    /** Bytes of firmware sent per chunk over BLE (matches the JBL app). */
    const val OTA_CHUNK_SIZE = 240

    fun getDeviceInfo(vararg features: Int): ByteArray =
        frame(CMD_GET_DEVICE_INFO, features.flatMap { listOf(it.toByte(), (it shr 8).toByte()) }.toByteArray())

    /** Ask the speaker for its OTA parameters; its reply signals it's ready to receive an update. */
    fun getOtaInfo(): ByteArray = getDeviceInfo(FEATURE_OTA_MAX_SIZE, FEATURE_OTA_PACKET_COUNT, FEATURE_OTA_PACKET_INTERVAL)

    /** Begin an update of a file of [fileSize] bytes. The speaker then drives it with OTA notifications. */
    fun startOta(fileSize: Int): ByteArray = frame(CMD_START_OTA, le32(fileSize))

    /** One firmware chunk: the absolute byte [offset] followed by the data at that offset. */
    fun sendOtaChunk(offset: Int, data: ByteArray): ByteArray = frame(CMD_SEND_OTA, le32(offset) + data)

    fun stopOta(): ByteArray = frame(CMD_STOP_OTA, byteArrayOf())

    private fun frame(command: Int, payload: ByteArray) = byteArrayOf(
        0x00, 0xDD.toByte(),
        command.toByte(), (command shr 8).toByte(),
        1, 0,
        payload.size.toByte(), (payload.size shr 8).toByte(),
        *payload
    )

    /** The command id of a received frame, or null if it isn't a Protocol 4 frame. */
    fun commandOf(bytes: ByteArray): Int? =
        if (bytes.size >= 4 && bytes[0] == 0x00.toByte() && bytes[1] == 0xDD.toByte()) u16(bytes, 2) else null

    /** Feature id -> value from a GET reply or notification, or null if [bytes] isn't one we can read. */
    fun parseFeatures(bytes: ByteArray): Map<Int, ByteArray>? {
        val command = commandOf(bytes) ?: return null
        if (command != CMD_GET_DEVICE_INFO && command != CMD_NOTIFICATION_TO_APP) return null
        if (bytes.size < 10) return null
        // ponytail: single-packet frames only; the features read so far are tiny. Reassemble by packet index if that changes.
        if (bytes[4].toInt() != 1 || u16(bytes, 6) != bytes.size - 8) return null
        if (u16(bytes, 8) != 0) return null // status code, 0 = OK

        val features = HashMap<Int, ByteArray>()
        var pos = 10
        while (pos + 4 <= bytes.size) {
            val length = u16(bytes, pos + 2)
            if (pos + 4 + length > bytes.size) return null
            features[u16(bytes, pos)] = bytes.copyOfRange(pos + 4, pos + 4 + length)
            pos += 4 + length
        }
        return features
    }

    /** status, percentage (out of 10000), absolute byte offset the speaker wants next, and how much it wants. */
    data class OtaNotification(val status: Int, val percentage: Int, val offset: Long, val lengthToRead: Long) {
        val isComplete get() = percentage == 10000 && offset == 0L && lengthToRead == 0L
    }

    fun parseOtaNotification(bytes: ByteArray): OtaNotification? {
        if (commandOf(bytes) != CMD_OTA_NOTIFICATION) return null
        if (bytes.size < 20 || u16(bytes, 6) < 12) return null // 8-byte header + 12-byte payload
        return OtaNotification(u16(bytes, 8), u16(bytes, 10), u32(bytes, 12), u32(bytes, 16))
    }

    private fun le32(v: Int) = byteArrayOf(v.toByte(), (v shr 8).toByte(), (v shr 16).toByte(), (v shr 24).toByte())
    private fun u16(bytes: ByteArray, at: Int) = (bytes[at].toInt() and 0xFF) or ((bytes[at + 1].toInt() and 0xFF) shl 8)
    private fun u32(bytes: ByteArray, at: Int): Long {
        var v = 0L
        for (i in 0 until 4) v = v or ((bytes[at + i].toLong() and 0xFF) shl (8 * i))
        return v
    }
}
