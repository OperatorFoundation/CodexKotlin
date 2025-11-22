package org.operatorfoundation.codex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.operatorfoundation.codex.symbols.*
import java.math.BigInteger

class SymbolTest {

    @Test
    fun testBinaryEncoding() {
        // Test encoding 0
        val encoded0 = Binary.encode(BigInteger.ZERO)
        assertEquals(0, encoded0.value)

        // Test encoding 1
        val encoded1 = Binary.encode(BigInteger.ONE)
        assertEquals(1, encoded1.value)

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            Binary.encode(BigInteger.valueOf(2))
        }
    }

    @Test
    fun testBinaryDecoding() {
        val binary0 = Binary.encode(BigInteger.ZERO)
        val binary1 = Binary.encode(BigInteger.ONE)

        assertEquals(BigInteger.ZERO, binary0.decode())
        assertEquals(BigInteger.ONE, binary1.decode())
    }

    @Test
    fun testBinaryRoundTrip() {
        val encoded = Binary.encode(BigInteger.ONE)
        val decoded = encoded.decode()
        assertEquals(BigInteger.ONE, decoded)
    }

    @Test
    fun testByteEncoding() {
        // Test various byte values
        val testValues = listOf(0, 1, 127, 128, 255)

        testValues.forEach { value ->
            val encoded = Octet.encode(BigInteger.valueOf(value.toLong()))
            assertEquals(value, encoded.value)
        }

        // Test invalid values
        assertThrows(IllegalArgumentException::class.java) {
            Octet.encode(BigInteger.valueOf(-1))
        }
        assertThrows(IllegalArgumentException::class.java) {
            Octet.encode(BigInteger.valueOf(256))
        }
    }

    @Test
    fun testByteDecoding() {
        val testValues = listOf(0, 1, 127, 128, 255)

        testValues.forEach { value ->
            val byteSymbol = Octet.encode(BigInteger.valueOf(value.toLong()))
            val decoded = byteSymbol.decode()
            assertEquals(BigInteger.valueOf(value.toLong()), decoded)
        }
    }

    @Test
    fun testByteRoundTrip() {
        (0..255).forEach { value ->
            val encoded = Octet.encode(BigInteger.valueOf(value.toLong()))
            val decoded = encoded.decode()
            assertEquals(BigInteger.valueOf(value.toLong()), decoded)
        }
    }

    @Test
    fun testCallAnyEncoding() {
        // Test encoding letters
        val encodedA = CallAny.encode(BigInteger.ZERO)
        assertEquals('A', encodedA.value)

        val encodedZ = CallAny.encode(BigInteger.valueOf(25))
        assertEquals('Z', encodedZ.value)

        // Test encoding numbers
        val encoded0 = CallAny.encode(BigInteger.valueOf(26))
        assertEquals('0', encoded0.value)

        val encoded9 = CallAny.encode(BigInteger.valueOf(35))
        assertEquals('9', encoded9.value)

        // Test encoding space
        val encodedSpace = CallAny.encode(BigInteger.valueOf(36))
        assertEquals(' ', encodedSpace.value)

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            CallAny.encode(BigInteger.valueOf(37))
        }
    }

    @Test
    fun testCallAnyDecoding() {
        assertEquals(BigInteger.ZERO, CallAny.encode(BigInteger.ZERO).decode())
        assertEquals(BigInteger.valueOf(25), CallAny.encode(BigInteger.valueOf(25)).decode())
        assertEquals(BigInteger.valueOf(26), CallAny.encode(BigInteger.valueOf(26)).decode())
        assertEquals(BigInteger.valueOf(35), CallAny.encode(BigInteger.valueOf(35)).decode())
        assertEquals(BigInteger.valueOf(36), CallAny.encode(BigInteger.valueOf(36)).decode())
    }

    @Test
    fun testCallLetterEncoding() {
        // Test all letters
        ('A'..'Z').forEachIndexed { index, char ->
            val encoded = CallLetter.encode(BigInteger.valueOf(index.toLong()))
            assertEquals(char, encoded.value)
        }

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            CallLetter.encode(BigInteger.valueOf(26))
        }
    }

    @Test
    fun testCallLetterDecoding() {
        ('A'..'Z').forEachIndexed { index, _ ->
            val encoded = CallLetter.encode(BigInteger.valueOf(index.toLong()))
            val decoded = encoded.decode()
            assertEquals(BigInteger.valueOf(index.toLong()), decoded)
        }
    }

    @Test
    fun testCallLetterNumberEncoding() {
        // Test letters
        val encodedA = CallLetterNumber.encode(BigInteger.ZERO)
        assertEquals('A', encodedA.value)

        val encodedZ = CallLetterNumber.encode(BigInteger.valueOf(25))
        assertEquals('Z', encodedZ.value)

        // Test numbers
        val encoded0 = CallLetterNumber.encode(BigInteger.valueOf(26))
        assertEquals('0', encoded0.value)

        val encoded9 = CallLetterNumber.encode(BigInteger.valueOf(35))
        assertEquals('9', encoded9.value)

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            CallLetterNumber.encode(BigInteger.valueOf(36))
        }
    }

    @Test
    fun testCallLetterNumberDecoding() {
        assertEquals(BigInteger.ZERO, CallLetterNumber.encode(BigInteger.ZERO).decode())
        assertEquals(BigInteger.valueOf(25), CallLetterNumber.encode(BigInteger.valueOf(25)).decode())
        assertEquals(BigInteger.valueOf(26), CallLetterNumber.encode(BigInteger.valueOf(26)).decode())
        assertEquals(BigInteger.valueOf(35), CallLetterNumber.encode(BigInteger.valueOf(35)).decode())
    }

    @Test
    fun testCallLetterSpaceEncoding() {
        // Test letters
        val encodedA = CallLetterSpace.encode(BigInteger.ZERO)
        assertEquals('A', encodedA.value)

        val encodedZ = CallLetterSpace.encode(BigInteger.valueOf(25))
        assertEquals('Z', encodedZ.value)

        // Test space
        val encodedSpace = CallLetterSpace.encode(BigInteger.valueOf(26))
        assertEquals(' ', encodedSpace.value)

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            CallLetterSpace.encode(BigInteger.valueOf(27))
        }
    }

    @Test
    fun testCallLetterSpaceDecoding() {
        assertEquals(BigInteger.ZERO, CallLetterSpace.encode(BigInteger.ZERO).decode())
        assertEquals(BigInteger.valueOf(25), CallLetterSpace.encode(BigInteger.valueOf(25)).decode())
        assertEquals(BigInteger.valueOf(26), CallLetterSpace.encode(BigInteger.valueOf(26)).decode())
    }

    @Test
    fun testGridLetterEncoding() {
        // Test A-R (18 letters)
        ('A'..'R').forEachIndexed { index, char ->
            val encoded = GridLetter.encode(BigInteger.valueOf(index.toLong()))
            assertEquals(char, encoded.value)
        }

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            GridLetter.encode(BigInteger.valueOf(18))
        }
    }

    @Test
    fun testGridLetterDecoding() {
        ('A'..'R').forEachIndexed { index, _ ->
            val encoded = GridLetter.encode(BigInteger.valueOf(index.toLong()))
            val decoded = encoded.decode()
            assertEquals(BigInteger.valueOf(index.toLong()), decoded)
        }
    }

    @Test
    fun testCallNumberEncoding() {
        // Test 0-9
        (0..9).forEach { value ->
            val encoded = CallNumber.encode(BigInteger.valueOf(value.toLong()))
            assertEquals(value.digitToChar(), encoded.value)
        }

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            CallNumber.encode(BigInteger.valueOf(10))
        }
    }

    @Test
    fun testCallNumberDecoding() {
        (0..9).forEach { value ->
            val encoded = CallNumber.encode(BigInteger.valueOf(value.toLong()))
            val decoded = encoded.decode()
            assertEquals(BigInteger.valueOf(value.toLong()), decoded)
        }
    }

    @Test
    fun testGridNumberEncoding() {
        // Test 0-9
        (0..9).forEach { value ->
            val encoded = GridNumber.encode(BigInteger.valueOf(value.toLong()))
            assertEquals(value.digitToChar(), encoded.value)
        }

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            GridNumber.encode(BigInteger.valueOf(10))
        }
    }

    @Test
    fun testGridNumberDecoding() {
        (0..9).forEach { value ->
            val encoded = GridNumber.encode(BigInteger.valueOf(value.toLong()))
            val decoded = encoded.decode()
            assertEquals(BigInteger.valueOf(value.toLong()), decoded)
        }
    }

    @Test
    fun testPowerEncoding() {
        val powerValues = listOf(
            0 to 0, 1 to 3, 2 to 7, 3 to 10, 4 to 13, 5 to 17,
            6 to 20, 7 to 23, 8 to 27, 9 to 30, 10 to 33, 11 to 37,
            12 to 40, 13 to 43, 14 to 47, 15 to 50, 16 to 53, 17 to 57,
            18 to 60
        )

        powerValues.forEach { (index, expectedValue) ->
            val encoded = Power.encode(BigInteger.valueOf(index.toLong()))
            assertEquals(expectedValue, encoded.value)
        }

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            Power.encode(BigInteger.valueOf(19))
        }
    }

    @Test
    fun testPowerDecoding() {
        (0..18).forEach { index ->
            val encoded = Power.encode(BigInteger.valueOf(index.toLong()))
            val decoded = encoded.decode()
            assertEquals(BigInteger.valueOf(index.toLong()), decoded)
        }
    }

    @Test
    fun testTrinaryEncoding() {
        // Test 0, 1, 2
        (0..2).forEach { value ->
            val encoded = Trinary.encode(BigInteger.valueOf(value.toLong()))
            assertEquals(value, encoded.value)
        }

        // Test invalid value
        assertThrows(IllegalArgumentException::class.java) {
            Trinary.encode(BigInteger.valueOf(3))
        }
    }

    @Test
    fun testTrinaryDecoding() {
        (0..2).forEach { value ->
            val encoded = Trinary.encode(BigInteger.valueOf(value.toLong()))
            val decoded = encoded.decode()
            assertEquals(BigInteger.valueOf(value.toLong()), decoded)
        }
    }

    @Test
    fun testWSPRMessageRoundTrip() {
        val testValues = listOf(
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.valueOf(1415934836),
            BigInteger.valueOf(123456789)
        )

        testValues.forEach { value ->
            val encoded = WSPRMessage.encode(value)

            // Verify it's a valid WSPR message (starts with Q)
            assertEquals('Q', encoded.prefix.value)

            val decoded = encoded.decode()
            assertEquals(value, decoded, "Round-trip failed for value $value")
        }
    }

    @Test
    fun testWSPRMessageSequenceRoundTrip() {
        val testValues = listOf(
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.valueOf(1000000),
            BigInteger("10000000000"),
            BigInteger("123456789012345678901234567890")
        )

        testValues.forEach { value ->
            // Create an empty sequence (it will be populated during encode)
            val encoded = WSPRMessageSequence.encode(value)

            // Verify we have at least one message
            assertTrue(encoded.messages.isNotEmpty(), "Sequence should have at least one message")

            // All messages should start with 'Q'
            encoded.messages.forEach { message ->
                assertEquals('Q', message.prefix.value, "Each message should start with Q")
            }

            val decoded = encoded.decode()
            assertEquals(value, decoded, "Round-trip failed for value $value")
        }
    }

    @Test
    fun testWSPRMessageSequenceMultipleMessages() {
        // Test that large values create multiple messages
        val largeValue = BigInteger("999999999999999999999")

        val encoded = WSPRMessageSequence.encode(largeValue)

        // Should have multiple messages for large value
        assertTrue(encoded.messages.size > 1, "Large value should create multiple messages")

        println("Large value $largeValue encoded into ${encoded.messages.size} messages")

        // Verify round-trip
        val decoded = encoded.decode()
        assertEquals(largeValue, decoded)
    }

    @Test
    fun testWSPRMessageSequenceSize() {
        val size = WSPRMessageSequence.size()

        // Size should be positive
        assertTrue(size > 0, "Sequence size should be positive")

        // Size should match individual component sizes
        val expectedSize = (CallLetterNumber.size() * 6) +
                (GridLetter.size() * 2) +
                (GridNumber.size() * 2) +
                Power.size()

        assertEquals(expectedSize, size, "Sequence size calculation incorrect")
    }

    @Test
    fun testAllSymbolSizes() {
        assertEquals(2, Binary.size())
        assertEquals(256, Octet.size())
        assertEquals(37, CallAny.size())
        assertEquals(26, CallLetter.size())
        assertEquals(36, CallLetterNumber.size())
        assertEquals(27, CallLetterSpace.size())
        assertEquals(18, GridLetter.size())
        assertEquals(10, CallNumber.size())
        assertEquals(10, GridNumber.size())
        assertEquals(19, Power.size())
        assertEquals(0, Required('Q').size())
        assertEquals(3, Trinary.size())
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