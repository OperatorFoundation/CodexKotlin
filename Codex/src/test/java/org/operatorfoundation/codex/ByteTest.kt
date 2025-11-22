//package org.operatorfoundation.codex
//
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.params.ParameterizedTest
//import org.junit.jupiter.params.provider.ValueSource
//import java.math.BigInteger
//
//class ByteTest
//{
//
//    private val symbol = Byte()
//
//    @Test
//    fun `size should be 256`() {
//        assertEquals(256, symbol.size())
//    }
//
//    @Test
//    fun `toString should return Byte`() {
//        assertEquals("Byte", symbol.toString())
//    }
//
//    @ParameterizedTest(name = "roundtrip {0}")
//    @ValueSource(ints = [0, 1, 2, 127, 128, 200, 254, 255])
//    fun `encode and decode roundtrip for selected values`(input: Int) {
//        val value = BigInteger.valueOf(input.toLong())
//        val encoded = symbol.encode(value)
//        val decoded = symbol.decode(encoded)
//        assertEquals(value, decoded)
//        assertEquals(1, encoded.size)
//    }
//
//    @Test
//    fun `encode and decode roundtrip for all values`() {
//        for (i in 0..255) {
//            val value = BigInteger.valueOf(i.toLong())        // 1) make a BigInteger 0..255
//            val encoded = symbol.encode(value)       // 2) encode it to bytes
//            val decoded = symbol.decode(encoded)    // 3) decode back to BigInteger
//            assertEquals(value,
//                decoded,
//                "Failed for $i")                        // 4) verify roundtrip
//        }
//    }
//
//    @Test
//    fun `encode should throw for negative values`() {
//        assertThrows(IllegalArgumentException::class.java) {
//            symbol.encode(BigInteger.valueOf(-1))
//        }
//    }
//
//    @Test
//    fun `encode should throw for values above 255`() {
//        assertThrows(IllegalArgumentException::class.java) {
//            symbol.encode(BigInteger.valueOf(256))
//        }
//    }
//
//    @Test
//    fun `decode should throw for arrays larger than 1 byte`() {
//        assertThrows(IllegalArgumentException::class.java) {
//            symbol.decode(byteArrayOf(1, 2))
//        }
//    }
//
//    @Test
//    fun `decode single byte 0 through 127`() {
//        for (i in 0..127) {
//            val input = byteArrayOf(i.toByte())
//            val decoded = symbol.decode(input)
//            assertEquals(BigInteger.valueOf(i.toLong()), decoded, "Failed for $i")
//        }
//    }
//
//    @Test
//    fun `decode single byte 128 through 255`() {
//        for (i in 128..255) {
//            val input = byteArrayOf(i.toByte()) // values above 127 wrap to negative Kotlin Bytes
//            val decoded = symbol.decode(input)
//            assertEquals(BigInteger.valueOf(i.toLong()), decoded, "Failed for $i")
//        }
//    }
//
//    @Test
//    fun `decode should throw when empty`() {
//        val ex = assertThrows(IllegalArgumentException::class.java) {
//            symbol.decode(byteArrayOf())
//        }
//        assertTrue(ex.message!!.contains("length"), "Expected message about length but got: ${ex.message}")
//    }
//
//    @Test
//    fun `decode should throw when more than 1 byte`() {
//        val ex = assertThrows(IllegalArgumentException::class.java) {
//            symbol.decode(byteArrayOf(1, 2, 3))
//        }
//        assertTrue(ex.message!!.contains("length"), "Expected message about length but got: ${ex.message}")
//    }
//}
