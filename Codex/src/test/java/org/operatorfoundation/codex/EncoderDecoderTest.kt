package org.operatorfoundation.codex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.operatorfoundation.codex.symbols.Binary
import org.operatorfoundation.codex.symbols.Symbol
import org.operatorfoundation.codex.symbols.Number
import org.operatorfoundation.codex.symbols.Byte
import org.operatorfoundation.codex.symbols.Required
import org.operatorfoundation.codex.symbols.CallLetterNumber
import org.operatorfoundation.codex.symbols.CallLetterSpace
import org.operatorfoundation.codex.symbols.GridLetter
import org.operatorfoundation.codex.symbols.Power
import org.operatorfoundation.codex.symbols.Trinary

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

    @Test
    fun testBinaryTrinaryWithRequired()
    {
        // Test with Required('A'), Binary(), Trinary()
        val encoder = Encoder(listOf(Required('A'.code.toByte()), Binary(), Trinary()))

        val testCases = listOf(
            0 to listOf("A", "0", "0"),
            1 to listOf("A", "0", "1"),
            2 to listOf("A", "0", "2"),
            3 to listOf("A", "1", "0"),
            4 to listOf("A", "1", "1"),
            5 to listOf("A", "1", "2")
        )

        testCases.forEach { (input, expected) ->
            val encoding = encoder.encode(BigInteger.valueOf(input.toLong()))
            val actual = encoding.map { it.decodeToString() }

            println("TEST RESULT input: $input, encoding: $actual")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun testBinaryTrinaryWithoutRequired()
    {
        // Test with just Binary(), Trinary()
        val encoder = Encoder(listOf(Binary(), Trinary()))

        val testCases = listOf(
            0 to listOf("0", "0"),
            1 to listOf("0", "1"),
            2 to listOf("0", "2"),
            3 to listOf("1", "0"),
            4 to listOf("1", "1"),
            5 to listOf("1", "2")
        )

        testCases.forEach { (input, expected) ->
            val encoding = encoder.encode(BigInteger.valueOf(input.toLong()))
            val actual = encoding.map { it.decodeToString() }

            println("TEST RESULT input: $input, encoding: $actual")

            assertEquals(expected, actual)
        }
    }

    @Test
    fun testRoundTrip()
    {
        // Test that encoding then decoding returns the original value
        val symbols = listOf(
            CallLetterNumber(),
            Binary(),
            Trinary(),
            Number(),
            CallLetterSpace()
        )

        val encoder = Encoder(symbols)
        val decoder = encoder.decoder()

        // Test various values
        val testValues = listOf(0, 1, 42, 100, 500, 1000, 5000)

        testValues.forEach { value ->
            val bigIntValue = BigInteger.valueOf(value.toLong())
            val encoded = encoder.encode(bigIntValue)
            val decoded = decoder.decode(encoded)

            assertEquals(bigIntValue, decoded, "Round-trip failed for value $value")
        }
    }


//    @Test
//    fun testRequiredSymbolValidation()
//    {
//        // Test that Required symbol validates correctly
//        val decoder = Decoder(listOf(Required('X'.code.toByte()), Number()))
//
//        // Should decode successfully with 'X'
//        val validInput = listOf("X".toByteArray(), "5".toByteArray())
//        val result = decoder.decode(validInput)
//        assertEquals(BigInteger.valueOf(5), result)
//
//        // Should throw with wrong required character
//        val invalidInput = listOf("Y".toByteArray(), "5".toByteArray())
//        assertThrows(IllegalArgumentException::class.java) {
//            decoder.decode(invalidInput)
//        }
//    }
//
//    @Test
//    fun testEncoderOverflow()
//    {
//        // Test that encoder throws when value is too large
//        val encoder = Encoder(listOf(Binary(), Binary())) // Max value = 3
//
//        assertThrows(Exception::class.java) {
//            encoder.encode(BigInteger.valueOf(4))
//        }
//    }

}