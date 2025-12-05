package org.operatorfoundation.codex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.operatorfoundation.codex.symbols.*
import java.math.BigInteger

class PackedWSPRMessageTest {

    @Test
    fun testPackedWSPRMessageByteCount() {
        // Verify that PackedWSPRMessage always returns 11 bytes
        val testValues = listOf(
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.valueOf(100),
            BigInteger.valueOf(1000000)
        )

        testValues.forEach { value ->
            val encoded = PackedWSPRMessage.encode(value)
            assertEquals(11, encoded.bytes.size, "PackedWSPRMessage should always be 11 bytes")
        }
    }

    @Test
    fun testPackedWSPRMessageZeroPadding() {
        // Verify that last 4 bytes are zero (convolutional tail)
        val encoded = PackedWSPRMessage.encode(BigInteger.valueOf(12345))

        // Bytes 7-10 should be zero
        assertEquals(0, encoded.bytes[7].toInt(), "Byte 7 should be zero")
        assertEquals(0, encoded.bytes[8].toInt(), "Byte 8 should be zero")
        assertEquals(0, encoded.bytes[9].toInt(), "Byte 9 should be zero")
        assertEquals(0, encoded.bytes[10].toInt(), "Byte 10 should be zero")
    }

    @Test
    fun testPackedWSPRMessageDataBits() {
        // Test that we have 50 bits of actual data (bytes 0-6 with partial byte 6)
        val testValue = BigInteger.valueOf(123456789)
        val encoded = PackedWSPRMessage.encode(testValue)

        // First 6 bytes should be fully used
        println("Testing data distribution for value: $testValue")
        println("Bytes 0-5 (should have data):")
        for (i in 0..5) {
            println("  Byte $i: ${encoded.bytes[i].toInt() and 0xFF} (0x${"%02X".format(encoded.bytes[i])})")
        }

        // Byte 6 should only use the top 2 bits (remaining 6 bits should be zero)
        val byte6 = encoded.bytes[6].toInt() and 0xFF
        println("Byte 6: $byte6 (0x${"%02X".format(encoded.bytes[6])}) - bottom 6 bits should be zero")
        assertEquals(0, byte6 and 0x3F, "Bottom 6 bits of byte 6 should be zero")
    }

    @Test
    fun testPackedWSPRMessageRoundTrip() {
        val testValues = listOf(
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.valueOf(100),
            BigInteger.valueOf(1000),
            BigInteger.valueOf(1000000),
            PackedWSPRMessage.size().subtract(BigInteger.ONE) // Max value
        )

        testValues.forEach { value ->
            val encoded = PackedWSPRMessage.encode(value)
            val decoded = encoded.decode()
            assertEquals(value, decoded, "Round-trip failed for value $value")
        }
    }

    @Test
    fun testPackedWSPRMessageSize() {
        println("\n=== Testing PackedWSPRMessage Size ===")

        val size = PackedWSPRMessage.size()
        println("PackedWSPRMessage.size() = $size")

        // Should be 2^50 since we have 50 data bits
        val expectedSize = BigInteger.TWO.pow(50)
        println("Expected size (2^50) = $expectedSize")

        assertEquals(expectedSize, size, "Size should be 2^50 for 50 data bits")
    }

    @Test
    fun testPackedWSPRMessageCapacity() {
        println("\n=== Testing PackedWSPRMessage Capacity ===")

        val size = PackedWSPRMessage.size()
        val maxValue = size.subtract(BigInteger.ONE)

        println("Max encodable value: $maxValue")

        // Test max value
        val encoded = PackedWSPRMessage.encode(maxValue)
        val decoded = encoded.decode()
        assertEquals(maxValue, decoded, "Max value should round-trip")
        println("✓ Max value encodes successfully")

        // Test that size (max + 1) fails
        assertThrows(IllegalArgumentException::class.java) {
            PackedWSPRMessage.encode(size)
        }
        println("✓ Value at size correctly throws exception")
    }

    @Test
    fun testPackedWSPRMessageZero() {
        println("\n=== Testing PackedWSPRMessage with ZERO ===")

        val encoded = PackedWSPRMessage.encode(BigInteger.ZERO)

        println("Encoded bytes:")
        encoded.bytes.forEachIndexed { i, byte ->
            println("  Byte $i: ${byte.toInt() and 0xFF} (0x${"%02X".format(byte)})")
        }

        val decoded = encoded.decode()
        assertEquals(BigInteger.ZERO, decoded, "Zero should round-trip correctly")
        println("✓ Zero round-trip successful")
    }

    @Test
    fun testPackedWSPRMessageOne() {
        println("\n=== Testing PackedWSPRMessage with ONE ===")

        val encoded = PackedWSPRMessage.encode(BigInteger.ONE)

        println("Encoded bytes:")
        encoded.bytes.forEachIndexed { i, byte ->
            println("  Byte $i: ${byte.toInt() and 0xFF} (0x${"%02X".format(byte)})")
        }

        val decoded = encoded.decode()
        assertEquals(BigInteger.ONE, decoded, "One should round-trip correctly")
        println("✓ One round-trip successful")
    }

    @Test
    fun testPackedWSPRMessageSequenceCapacity() {
        println("\n=== Testing PackedWSPRMessageSequence Capacity Growth ===")

        val singleMessageMax = PackedWSPRMessage.size().subtract(BigInteger.ONE)

        println("Single packed message max: $singleMessageMax")

        val testCases = listOf(
            "Max of 1 message" to singleMessageMax,
            "Min of 2 messages" to singleMessageMax.add(BigInteger.ONE),
            "Max of 2 messages" to PackedWSPRMessage.size().pow(2).subtract(BigInteger.ONE)
        )

        testCases.forEach { (description, value) ->
            println("\nTesting: $description")
            println("  Value: $value")

            val encoded = PackedWSPRMessageSequence.encode(value)
            println("  Messages: ${encoded.messages.size}")

            val decoded = encoded.decode()
            assertEquals(value, decoded, "$description should round-trip correctly")
            println("  ✓ Round-trip successful")
        }
    }

    @Test
    fun testPackedWSPRMessageSequenceMultipleMessages() {
        println("\n=== Testing PackedWSPRMessageSequence Multiple Messages ===")

        val singleMessageMax = PackedWSPRMessage.size().subtract(BigInteger.ONE)
        val largeValue = singleMessageMax.multiply(BigInteger.valueOf(10))

        println("Single message max: $singleMessageMax")
        println("Testing value: $largeValue")

        val encoded = PackedWSPRMessageSequence.encode(largeValue)

        println("Encoded into ${encoded.messages.size} messages")

        assertTrue(encoded.messages.size > 1, "Large value should create multiple messages")

        val decoded = encoded.decode()
        assertEquals(largeValue, decoded)
        println("✓ Round-trip successful")
    }

    @Test
    fun testPackedWSPRMessageSequenceZero() {
        println("\n=== Testing PackedWSPRMessageSequence with Zero ===")

        val encoded = PackedWSPRMessageSequence.encode(BigInteger.ZERO)

        println("Zero encoded into ${encoded.messages.size} message(s)")
        assertEquals(1, encoded.messages.size, "Zero should encode to exactly 1 message")

        // Verify all bytes in the single message
        println("Message bytes:")
        encoded.messages[0].bytes.forEachIndexed { i, byte ->
            println("  Byte $i: ${byte.toInt() and 0xFF} (0x${"%02X".format(byte)})")
        }

        val decoded = encoded.decode()
        assertEquals(BigInteger.ZERO, decoded, "Zero should round-trip correctly")
        println("✓ Zero round-trip successful")
    }

    @Test
    fun testPackedWSPRMessageSequenceSimpleMessage() {
        println("\n=== Testing PackedWSPRMessageSequence with Large Value ===")

        val largeValue = BigInteger("295487912345699009911221199008899778866771122334455667788")

        val encoded = PackedWSPRMessageSequence.encode(largeValue)

        assertTrue(encoded.messages.size > 1, "Large value should create multiple messages")
        println("Large value encoded into ${encoded.messages.size} messages")

        val decoded = encoded.decode()
        assertEquals(largeValue, decoded)
        println("✓ Round-trip successful")
    }

    @Test
    fun testPackedWSPRMessageSequenceBoundary() {
        println("\n=== PackedWSPRMessageSequence Boundary Test ===")

        val messageSize = PackedWSPRMessage.size()
        println("PackedWSPRMessage.size(): $messageSize")

        // Test max value for single message
        val maxSingleMessage = messageSize.subtract(BigInteger.ONE)
        println("\n--- Testing max single message value ---")
        println("Value: $maxSingleMessage")

        val encoded = PackedWSPRMessageSequence.encode(maxSingleMessage)
        println("Encoded into ${encoded.messages.size} message(s)")
        assertEquals(1, encoded.messages.size, "Should fit in 1 message")

        val decoded = encoded.decode()
        assertEquals(maxSingleMessage, decoded, "Max single message value should round-trip")
        println("✓ Round-trip successful")

        // Test min value for two messages
        println("\n--- Testing min two message value ---")
        val minTwoMessages = messageSize
        println("Value: $minTwoMessages")

        val encoded2 = PackedWSPRMessageSequence.encode(minTwoMessages)
        println("Encoded into ${encoded2.messages.size} message(s)")
        assertEquals(2, encoded2.messages.size, "Should require 2 messages")

        val decoded2 = encoded2.decode()
        assertEquals(minTwoMessages, decoded2, "Min two message value should round-trip")
        println("✓ Round-trip successful")
    }

    @Test
    fun testPackedWSPRMessageSequenceVerySimple() {
        println("\n=== Very Simple PackedWSPRMessageSequence Test ===")

        val messageSize = PackedWSPRMessage.size()
        println("PackedWSPRMessage.size(): $messageSize")

        val testValues = listOf(
            "ZERO" to BigInteger.ZERO,
            "ONE" to BigInteger.ONE,
            "100" to BigInteger.valueOf(100),
            "1000" to BigInteger.valueOf(1000)
        )

        testValues.forEach { (name, value) ->
            println("\n--- Testing $name ---")
            val encoded = PackedWSPRMessageSequence.encode(value)
            println("Encoded into ${encoded.messages.size} message(s)")

            val decoded = encoded.decode()
            println("Decoded value: $decoded")
            assertEquals(value, decoded, "$name should round-trip correctly")
            println("✓ Match")
        }
    }

    @Test
    fun testPackedWSPRMessageBitPacking() {
        println("\n=== Testing Bit Packing Details ===")

        // Test a known value to verify bit packing
        val testValue = BigInteger.valueOf(0x3FFFFFFFFFFFF) // 50 bits all set

        println("Test value: $testValue")
        println("Binary: ${testValue.toString(2).padStart(50, '0')}")

        val encoded = PackedWSPRMessage.encode(testValue)

        println("\nEncoded bytes:")
        encoded.bytes.forEachIndexed { i, byte ->
            val unsigned = byte.toInt() and 0xFF
            println("  Byte $i: $unsigned (0x${"%02X".format(byte)}) = ${unsigned.toString(2).padStart(8, '0')}")
        }

        val decoded = encoded.decode()
        assertEquals(testValue, decoded, "Bit packing should preserve value")
        println("\n✓ Bit packing verified")
    }

    @Test
    fun testPackedWSPRMessageConsistency() {
        println("\n=== Testing Consistency Between Encodings ===")

        // Same value should always produce same bytes
        val value = BigInteger.valueOf(123456789)

        val encoded1 = PackedWSPRMessage.encode(value)
        val encoded2 = PackedWSPRMessage.encode(value)

        println("Encoding value twice: $value")
        println("First encoding:")
        encoded1.bytes.forEachIndexed { i, byte ->
            println("  Byte $i: ${byte.toInt() and 0xFF}")
        }

        println("Second encoding:")
        encoded2.bytes.forEachIndexed { i, byte ->
            println("  Byte $i: ${byte.toInt() and 0xFF}")
        }

        assertArrayEquals(encoded1.bytes, encoded2.bytes, "Same value should produce same bytes")
        println("✓ Encodings are consistent")
    }

    @Test
    fun testPackedWSPRMessageSequenceEncodingStep() {
        println("\n=== Step-by-step Packed Sequence Encoding ===")

        val messageSize = PackedWSPRMessage.size()
        println("messageSize: $messageSize")

        val testValue = messageSize.add(BigInteger.valueOf(42)) // Just over 1 message
        println("testValue: $testValue")
        println("Should fit in: 2 messages")
        println()

        var remaining = testValue
        var iteration = 0

        println("Starting encode loop:")
        do {
            iteration++
            val value = remaining.mod(messageSize)
            println("  Iteration $iteration:")
            println("    remaining: $remaining")
            println("    value (mod messageSize): $value")

            try {
                val message = PackedWSPRMessage.encode(value)
                println("    ✓ Encoded message successfully (11 bytes)")
            } catch (e: Exception) {
                println("    ✗ Failed to encode: ${e.message}")
            }

            remaining = remaining.divide(messageSize)
            println("    new remaining: $remaining")
            println()

        } while (remaining > BigInteger.ZERO)

        println("Total iterations: $iteration")
        assertEquals(2, iteration, "Should take exactly 2 iterations")
    }
}