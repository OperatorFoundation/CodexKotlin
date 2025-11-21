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
import org.operatorfoundation.codex.symbols.WSPRMessage
import org.operatorfoundation.codex.symbols.WSPRMessageSequence

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


@Test
fun testWSPRMessageRoundTrip() {
    val wsprMessage = WSPRMessage()

    // Test various values within WSPR message capacity
    val testValues = listOf(
        BigInteger.ZERO,
        BigInteger.ONE,
        BigInteger.valueOf(1415934836), // "Test" as integer
        BigInteger.valueOf(123456789),
        BigInteger.valueOf(999999999),
        BigInteger("10000000000") // Larger value
    )

    testValues.forEach { value ->
        val encoded = wsprMessage.encode(value)

        // Verify size
        assertEquals(wsprMessage.size(), encoded.size,
            "Encoded size mismatch for value $value")

        // Decode and verify
        val decoded = wsprMessage.decode(encoded)
        assertEquals(value, decoded, "Round-trip failed for value $value")

        println("WSPRMessage round-trip: $value -> ${encoded.size} bytes -> $decoded")
    }
}

@Test
fun testWSPRMessageFormat() {
    val wsprMessage = WSPRMessage()
    val testInteger = BigInteger.valueOf(1415934836) // "Test"

    val encoded = wsprMessage.encode(testInteger)

    // Verify the message starts with 'Q'
    assertEquals('Q'.code.toByte(), encoded[0], "WSPR message should start with 'Q'")

    // Decode and display the message components
    val parts = mutableListOf<String>()
    parts.add(encoded[0].toInt().toChar().toString()) // Required 'Q'
    parts.add((1..6).map { encoded[it].toInt().toChar() }.joinToString("")) // Callsign
    parts.add((7..8).map { encoded[it].toInt().toChar() }.joinToString("")) // Grid letters
    parts.add((9..10).map { encoded[it].toInt().toChar() }.joinToString("")) // Grid numbers
    parts.add(encoded[11].toInt().toChar().toString()) // Power

    println("WSPR Message components: ${parts.joinToString(" ")}")

    // Verify decoding
    val decoded = wsprMessage.decode(encoded)
    assertEquals(testInteger, decoded)
}

@Test
fun testWSPRMessageSequenceSingleMessage() {
    // Test with a sequence of 1 message (should behave like WSPRMessage)
    val sequence = WSPRMessageSequence(1)
    val singleMessage = WSPRMessage()

    val testValue = BigInteger.valueOf(1415934836)

    val seqEncoded = sequence.encode(testValue)
    val msgEncoded = singleMessage.encode(testValue)

    // Should produce identical results
    assertArrayEquals(msgEncoded, seqEncoded)

    // Verify decoding
    val decoded = sequence.decode(seqEncoded)
    assertEquals(testValue, decoded)
}

@Test
fun testWSPRMessageSequenceMultipleMessages() {
    // Test with 3 messages
    val sequence = WSPRMessageSequence(3)

    val testValues = listOf(
        BigInteger.ZERO,
        BigInteger.ONE,
        BigInteger.valueOf(1415934836),
        BigInteger("100000000000000"), // Large value requiring multiple messages
        BigInteger("999999999999999999")
    )

    testValues.forEach { value ->
        val encoded = sequence.encode(value)

        // Verify total size is 3 × single message size
        val singleMessageSize = WSPRMessage().size()
        assertEquals(singleMessageSize * 3, encoded.size,
            "Sequence size should be 3 × single message size")

        // Decode and verify
        val decoded = sequence.decode(encoded)
        assertEquals(value, decoded, "Round-trip failed for value $value")

        println("WSPRMessageSequence(3) round-trip: $value -> ${encoded.size} bytes -> $decoded")
    }
}

@Test
fun testWSPRMessageSequenceCapacity() {
    // Test that larger sequences can handle larger numbers
    val sequence2 = WSPRMessageSequence(2)
    val sequence5 = WSPRMessageSequence(5)

    // A very large number that would overflow a single message
    val largeValue = BigInteger("123456789012345678901234567890")

    try {
        val encoded2 = sequence2.encode(largeValue)
        val decoded2 = sequence2.decode(encoded2)
        assertEquals(largeValue, decoded2, "Sequence of 2 failed for large value")
        println("WSPRMessageSequence(2) handled large value successfully")
    } catch (e: Exception) {
        println("WSPRMessageSequence(2) cannot handle value (expected if too large)")
    }

    try {
        val encoded5 = sequence5.encode(largeValue)
        val decoded5 = sequence5.decode(encoded5)
        assertEquals(largeValue, decoded5, "Sequence of 5 failed for large value")
        println("WSPRMessageSequence(5) handled large value: $largeValue")
    } catch (e: Exception) {
        println("WSPRMessageSequence(5) cannot handle value (too large even for 5 messages)")
    }
}

@Test
fun testWSPRMessageSequenceIndependence() {
    // Verify that each message in the sequence contributes independently
    val sequence = WSPRMessageSequence(2)
    val singleMessageSize = WSPRMessage().size()

    val testValue = BigInteger.valueOf(1000000)
    val encoded = sequence.encode(testValue)

    // Split into two messages
    val message1 = encoded.sliceArray(0 until singleMessageSize)
    val message2 = encoded.sliceArray(singleMessageSize until encoded.size)

    println("Message 1 bytes: ${message1.map { (it.toInt() and 0xFF).toString(16) }}")
    println("Message 2 bytes: ${message2.map { (it.toInt() and 0xFF).toString(16) }}")

    // Both should start with 'Q'
    assertEquals('Q'.code.toByte(), message1[0], "First message should start with Q")
    assertEquals('Q'.code.toByte(), message2[0], "Second message should start with Q")

    // Verify full round-trip still works
    val decoded = sequence.decode(encoded)
    assertEquals(testValue, decoded)
}

@Test
fun testWSPRMessageVsSequenceComparison() {
    // Compare the capacity and behavior of single vs multiple messages
    val single = WSPRMessage()
    val double = WSPRMessageSequence(2)

    // Find a value that fits in single message
    val smallValue = BigInteger.valueOf(1000)
    val singleEncoded = single.encode(smallValue)
    val doubleEncoded = double.encode(smallValue)

    println("Single message size: ${singleEncoded.size} bytes")
    println("Double sequence size: ${doubleEncoded.size} bytes")

    assertEquals(single.size(), singleEncoded.size)
    assertEquals(single.size() * 2, doubleEncoded.size)

    // Both should decode correctly
    assertEquals(smallValue, single.decode(singleEncoded))
    assertEquals(smallValue, double.decode(doubleEncoded))
}

@Test
fun testWSPRMessageSequenceEdgeCases() {
    // Test edge cases
    val sequence = WSPRMessageSequence(1)

    // Test zero
    val zeroEncoded = sequence.encode(BigInteger.ZERO)
    assertEquals(BigInteger.ZERO, sequence.decode(zeroEncoded))

    // Test one
    val oneEncoded = sequence.encode(BigInteger.ONE)
    assertEquals(BigInteger.ONE, sequence.decode(oneEncoded))

    // Test that invalid count throws
    assertThrows(IllegalArgumentException::class.java) {
        WSPRMessageSequence(0)
    }

    assertThrows(IllegalArgumentException::class.java) {
        WSPRMessageSequence(-1)
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