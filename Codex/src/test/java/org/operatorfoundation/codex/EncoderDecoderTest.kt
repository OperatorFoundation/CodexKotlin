package org.operatorfoundation.codex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.operatorfoundation.codex.symbols.Symbol
import org.operatorfoundation.codex.symbols.Number
import org.operatorfoundation.codex.symbols.Byte
import org.operatorfoundation.codex.symbols.Required
import org.operatorfoundation.codex.symbols.CallLetterNumber
import org.operatorfoundation.codex.symbols.CallLetterSpace
import org.operatorfoundation.codex.symbols.GridLetter
import org.operatorfoundation.codex.symbols.Power

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

    @Test
    fun testWSPREncoding() {
        // First get test integer from byte encoding
        val bytesDecoder = Decoder(listOf(Byte(), Byte(), Byte(), Byte()))
        val testByteArrays = "Test".toByteArray().map { byteArrayOf(it) }
        val testInteger = bytesDecoder.decode(testByteArrays)

        // Create WSPR encoder - explicitly type as List<Symbol>
        val wsprEncoder = Encoder(listOf<Symbol>(
            Required('Q'.code.toByte()),
            CallLetterNumber(),
            Number(),
            CallLetterSpace(),
            CallLetterSpace(),
            CallLetterSpace(),
            GridLetter(),
            GridLetter(),
            Number(),
            Number(),
            Power()
        ))

        // Encode the integer
        val wsprEncoding = wsprEncoder.encode(testInteger)
        println(wsprEncoding.map { it.decodeToString() })

        // Format output like Python
        val callsign = wsprEncoding.subList(0, 6).joinToString("") { it.decodeToString() }
        val grid = wsprEncoding.subList(6, 10).joinToString("") { it.decodeToString() }
        val power = wsprEncoding[10].decodeToString()

        println("WSPR Message: $callsign $grid $power")

        // Verify decoding back to same integer
        val wsprDecoder = wsprEncoder.decoder()
        val decodedInteger = wsprDecoder.decode(wsprEncoding)
        println("$ i: $decodedInteger")

        assertEquals(testInteger, decodedInteger)

        // Encode back to bytes and verify
        val bytesEncoder = bytesDecoder.encoder()
        val finalBytes = bytesEncoder.encode(decodedInteger)
        val finalString = String(finalBytes.map { it[0] }.toByteArray())

        println("% bs: ${finalBytes.map { it[0].toInt() and 0xFF }}, str: $finalString")

        assertEquals("Test", finalString)
    }

}