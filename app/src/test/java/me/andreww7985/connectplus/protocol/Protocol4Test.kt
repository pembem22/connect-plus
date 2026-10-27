package me.andreww7985.connectplus.protocol

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class Protocol4Test {
    private fun bytes(vararg values: Int) = ByteArray(values.size) { values[it].toByte() }

    @Test
    fun firmwareVersionRequestMatchesJblApp() {
        assertArrayEquals(bytes(0x00, 0xDD, 0x01, 0x00, 0x01, 0x00, 0x02, 0x00, 0x0C, 0x00),
            Protocol4.getDeviceInfo(Protocol4.FEATURE_FIRMWARE_VERSION))
    }

    @Test
    fun parsesGetReply() {
        val reply = bytes(
            0x00, 0xDD, 0x01, 0x00, 0x01, 0x00, 0x15, 0x00,
            0x00, 0x00, // status OK
            0x0C, 0x00, 0x03, 0x00, 26, 16, 10, // firmware 26.16.10
            0x0D, 0x00, 0x01, 0x00, 0xE4, // battery 100%, charging
            0x06, 0x00, 0x03, 0x00, 'B'.code, 'B'.code, '4'.code
        )
        val features = Protocol4.parseFeatures(reply)!!
        assertArrayEquals(bytes(26, 16, 10), features[Protocol4.FEATURE_FIRMWARE_VERSION])
        assertArrayEquals(bytes(0xE4), features[Protocol4.FEATURE_BATTERY_STATUS])
        assertEquals("BB4", String(features[Protocol4.FEATURE_DEVICE_NAME]!!))

        reply[8] = 1 // non-zero status
        assertNull(Protocol4.parseFeatures(reply))
    }

    @Test
    fun otaInfoRequestAsksForOtaFeatures() {
        // features 0x0B00, 0x0B01, 0x0003 as little-endian pairs
        assertArrayEquals(bytes(0x00, 0xDD, 0x01, 0x00, 0x01, 0x00, 0x06, 0x00, 0x00, 0x0B, 0x01, 0x0B, 0x03, 0x00),
            Protocol4.getOtaInfo())
    }

    @Test
    fun startOtaCarriesLittleEndianFileSize() {
        assertArrayEquals(bytes(0x00, 0xDD, 0x01, 0x01, 0x01, 0x00, 0x04, 0x00, 0x04, 0x03, 0x02, 0x01),
            Protocol4.startOta(0x01020304))
    }

    @Test
    fun sendOtaChunkPrefixesOffset() {
        assertArrayEquals(
            bytes(0x00, 0xDD, 0x02, 0x01, 0x01, 0x00, 0x06, 0x00, 0x00, 0x01, 0x00, 0x00, 0xAA, 0xBB),
            Protocol4.sendOtaChunk(0x100, bytes(0xAA, 0xBB)))
        assertArrayEquals(bytes(0x00, 0xDD, 0x03, 0x01, 0x01, 0x00, 0x00, 0x00), Protocol4.stopOta())
    }

    @Test
    fun parsesOtaNotification() {
        val n = Protocol4.parseOtaNotification(bytes(
            0x00, 0xDD, 0x04, 0x01, 0x01, 0x00, 0x0C, 0x00,
            0x00, 0x00,             // status
            0x88, 0x13,             // percentage 5000
            0x00, 0x0F, 0x00, 0x00, // offset 0xF00
            0xF0, 0x00, 0x00, 0x00  // lengthToRead 240
        ))!!
        assertEquals(5000, n.percentage)
        assertEquals(0xF00L, n.offset)
        assertEquals(240L, n.lengthToRead)
        assertEquals(false, n.isComplete)

        // completion signal: 100% and nothing more to read
        val done = Protocol4.parseOtaNotification(bytes(
            0x00, 0xDD, 0x04, 0x01, 0x01, 0x00, 0x0C, 0x00,
            0x00, 0x00, 0x10, 0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00))!!
        assertEquals(true, done.isComplete)

        assertNull(Protocol4.parseOtaNotification(bytes(0x00, 0xDD, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00)))
    }
}
