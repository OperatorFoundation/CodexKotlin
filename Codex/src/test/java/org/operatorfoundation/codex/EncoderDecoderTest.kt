package org.operatorfoundation.codex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.operatorfoundation.codex.symbols.*
import java.math.BigInteger

class EncoderDecoderTest
{
    @Test
    fun testByteEncodingDecoding()
    {
        // Create decoder with 4 byte symbols
        val bytesDecoder = Decoder(listOf(Byte(), Byte(), Byte(), Byte()))

        // Create "Test" as bytes
        val testBytes = "Test".toByteArray()
        val testByteArrays = testBytes.map { byteArrayOf(it) }

        // Decode to integer
        val decodedInteger = bytesDecoder.decode(testByteArrays)
        println("@ i: $decodedInteger")

        // Verify the decoded value matches Python output
        // In Python: b'Test' decodes to 1415934836
        assertEquals(BigInteger.valueOf(1415934836), decodedInteger)

        // Create encoder and encode back
        val bytesEncoder = bytesDecoder.encoder()
        val encodedBytes = bytesEncoder.encode(decodedInteger)

        // Convert back to string for verification
        val reconstructedBytes = encodedBytes.map { it[0] }.toByteArray()
        val reconstructedString = String(reconstructedBytes)

        println("% bs: ${encodedBytes.map { it[0].toInt() and 0xFF }}, str: $reconstructedString")

        assertEquals("Test", reconstructedString)
    }

}