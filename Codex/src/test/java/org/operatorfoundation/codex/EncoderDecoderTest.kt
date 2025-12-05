package org.operatorfoundation.codex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.operatorfoundation.codex.symbols.*
import java.math.BigInteger

class SymbolTest {

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
    fun testWSPRMessageSequenceCapacity() {
        println("\n=== Testing WSPRMessageSequence Capacity Growth ===")

        val singleMessageMax = WSPRMessage.size().subtract(BigInteger.ONE)

        println("Single WSPR message max: $singleMessageMax")

        // Test values at boundaries
        val testCases = listOf(
            "Max of 1 message" to singleMessageMax,
            "Min of 2 messages" to singleMessageMax.add(BigInteger.ONE),
            "Max of 2 messages" to WSPRMessage.size().pow(2).subtract(BigInteger.ONE)
        )

        testCases.forEach { (description, value) ->
            println("\nTesting: $description")
            println("  Value: $value")

            val encoded = WSPRMessageSequence.encode(value)
            println("  Messages: ${encoded.messages.size}")

            val decoded = encoded.decode()
            assertEquals(value, decoded, "$description should round-trip correctly")
            println("  ✓ Round-trip successful")
        }
    }

    @Test
    fun testWSPRMessageSequenceMultipleMessages() {
        println("\n=== Testing WSPRMessageSequence Multiple Messages ===")

        // Test that large values create multiple messages
        val singleMessageMax = WSPRMessage.size().subtract(BigInteger.ONE)
        val largeValue = singleMessageMax.multiply(BigInteger.valueOf(10))

        println("Single message max: $singleMessageMax")
        println("Testing value: $largeValue")

        val encoded = WSPRMessageSequence.encode(largeValue)

        println("Encoded into ${encoded.messages.size} messages")

        // Should have multiple messages for large value
        assertTrue(encoded.messages.size > 1, "Large value should create multiple messages")

        // Verify round-trip
        val decoded = encoded.decode()
        assertEquals(largeValue, decoded)
        println("✓ Round-trip successful")
    }

    @Test
    fun testWSPRMessageSequenceSimpleMessage() {
        // Test that large values create multiple messages
        val largeValue = BigInteger("295487912345699009911221199008899778866771122334455667788")

        val encoded = WSPRMessageSequence.encode(largeValue)

        // Should have multiple messages for large value
        assertTrue(encoded.messages.size > 1, "Large value should create multiple messages")

        println("Large value $largeValue encoded into ${encoded.messages.size} messages")

        // Verify round-trip
        val decoded = encoded.decode()
        assertEquals(largeValue, decoded)
    }

    @Test
    fun testWSPRMessageSequenceZero() {
        println("\n=== Testing WSPRMessageSequence with Zero ===")

        val encoded = WSPRMessageSequence.encode(BigInteger.ZERO)

        println("Zero encoded into ${encoded.messages.size} message(s)")
        assertEquals(1, encoded.messages.size, "Zero should encode to exactly 1 message")

        val decoded = encoded.decode()
        assertEquals(BigInteger.ZERO, decoded, "Zero should round-trip correctly")
        println("✓ Zero round-trip successful")
    }

    @Test
    fun testAllSymbolSizes() {
        assertEquals(256, Octet.size().toInt())
        assertEquals(37, CallAny.size().toInt())
        assertEquals(26, CallLetter.size().toInt())
        assertEquals(36, CallLetterNumber.size().toInt())
        assertEquals(27, CallLetterSpace.size().toInt())
        assertEquals(18, GridLetter.size().toInt())
        assertEquals(10, CallNumber.size().toInt())
        assertEquals(10, GridNumber.size().toInt())
        assertEquals(19, Power.size().toInt())
        assertEquals(0, Required('Q').size().toInt())
    }

    @Test
    fun testWSPRMessageBasicEncoding() {
        // Test encoding small values
        println("\n=== Testing WSPRMessage Basic Encoding ===")

        val testValues = listOf(0, 1, 2, 3, 4, 5, 10, 100)

        testValues.forEach { value ->
            println("\nTesting value: $value")

            try {
                val encoded = WSPRMessage.encode(BigInteger.valueOf(value.toLong()))

                println("  Encoded successfully")
                println("  Prefix: ${encoded.prefix.value}")
                println("  Callsign: ${encoded.callsign1.value}${encoded.callsign2.value}${encoded.callsign3.value}${encoded.callsign4.value}${encoded.callsign5.value}")
                println("  Grid: ${encoded.grid1.value}${encoded.grid2.value}${encoded.grid3.value}${encoded.grid4.value}")
                println("  Power: ${encoded.power.value}")

                // Try to decode it back
                val decoded = encoded.decode()
                println("  Decoded: $decoded")

                assertEquals(BigInteger.valueOf(value.toLong()), decoded,
                    "Round-trip failed for value $value")
                println("  ✓ Round-trip successful")

            } catch (e: Exception) {
                println("  ✗ Error: ${e.message}")
                e.printStackTrace()
                throw e
            }
        }
    }

    @Test
    fun testWSPRMessageZero() {
        println("\n=== Testing WSPRMessage with ZERO ===")

        val encoded = WSPRMessage.encode(BigInteger.ZERO)

        println("Prefix: ${encoded.prefix.value}")
        println("Callsign: ${encoded.callsign1.value}${encoded.callsign2.value}${encoded.callsign3.value}${encoded.callsign4.value}${encoded.callsign5.value}")
        println("Grid: ${encoded.grid1.value}${encoded.grid2.value}${encoded.grid3.value}${encoded.grid4.value}")
        println("Power: ${encoded.power.value}")

        val decoded = encoded.decode()
        println("Decoded: $decoded")

        assertEquals(BigInteger.ZERO, decoded, "Zero should round-trip correctly")
    }

    @Test
    fun testWSPRMessageOne() {
        println("\n=== Testing WSPRMessage with ONE ===")

        val encoded = WSPRMessage.encode(BigInteger.ONE)

        println("Prefix: ${encoded.prefix.value}")
        println("Callsign: ${encoded.callsign1.value}${encoded.callsign2.value}${encoded.callsign3.value}${encoded.callsign4.value}${encoded.callsign5.value}")
        println("Grid: ${encoded.grid1.value}${encoded.grid2.value}${encoded.grid3.value}${encoded.grid4.value}")
        println("Power: ${encoded.power.value}")

        val decoded = encoded.decode()
        println("Decoded: $decoded")

        assertEquals(BigInteger.ONE, decoded, "One should round-trip correctly")
    }

    @Test
    fun testWSPRMessageSize() {
        println("\n=== Testing WSPRMessage Size ===")

        val size = WSPRMessage.size()
        println("WSPRMessage.size() = $size")

        // Calculate expected size
        val expectedSize = (CallLetterNumber.size() * 6.toBigInteger()) +
                (GridLetter.size() * 2.toBigInteger()) +
                (GridNumber.size() * 2.toBigInteger()) +
                Power.size()

        println("Expected size = $expectedSize")
        println("  CallLetterNumber: ${CallLetterNumber.size()} × 6 = ${CallLetterNumber.size() * 6.toBigInteger()}")
        println("  GridLetter: ${GridLetter.size()} × 2 = ${GridLetter.size() * 2.toBigInteger()}")
        println("  GridNumber: ${GridNumber.size()} × 2 = ${GridNumber.size() * 2.toBigInteger()}")
        println("  Power: ${Power.size()}")

        assertTrue(size > 0.toBigInteger(), "Size should be positive")
    }

    @Test
    fun testWSPRMessageCapacity() {
        println("\n=== Testing WSPRMessage Capacity ===")

        val size = WSPRMessage.size()

        // Maximum value that can be encoded is size - 1
        val maxValue = size.subtract(BigInteger.ONE)

        println("WSPRMessage.size() = $size")
        println("Maximum encodable value = $maxValue")

        // Test max value
        try {
            val encoded = WSPRMessage.encode(maxValue)
            val decoded = encoded.decode()
            assertEquals(maxValue, decoded, "Max value should round-trip")
            println("✓ Max value ($maxValue) encodes successfully")
        } catch (e: Exception) {
            println("✗ Max value failed: ${e.message}")
            throw e
        }

        // Test that size (max + 1) fails
        try {
            WSPRMessage.encode(size)
            fail("Value at size ($size) should throw exception")
        } catch (e: IllegalArgumentException) {
            println("✓ Value at size correctly throws exception")
        }
    }

    @Test
    fun testWSPRMessageBisectionSearch() {
        println("\n=== Bisection Search for WSPRMessage Failure Point ===")

        val size = WSPRMessage.size()
        println("WSPRMessage size: $size")

        // Start with a range we know: 100 works, let's find where it breaks
        var low = BigInteger.valueOf(100) // Known to work
        var high = BigInteger.valueOf(size.toLong()).pow(11) // Suspected to fail

        println("Initial range: $low to $high")

        // First verify the bounds
        println("\nVerifying lower bound ($low)...")
        assertTrue(testValue(low), "Lower bound should work")

        println("\nVerifying upper bound ($high)...")
        val highWorks = testValue(high)
        if (highWorks) {
            println("Upper bound works! No failure found in expected range.")
            return
        }

        println("\nStarting bisection search...")
        var lastWorking = low
        var firstFailing = high
        var iterations = 0

        while (high.subtract(low) > BigInteger.ONE) {
            iterations++
            val mid = low.add(high).divide(BigInteger.TWO)

            println("\nIteration $iterations:")
            println("  Testing: $mid")
            println("  Range: [$low, $high]")
            println("  Difference: ${high.subtract(low)}")

            if (testValue(mid)) {
                println("  ✓ Value works")
                low = mid
                lastWorking = mid
            } else {
                println("  ✗ Value fails")
                high = mid
                firstFailing = mid
            }
        }

        println("\n=== RESULTS ===")
        println("Last working value: $lastWorking")
        println("First failing value: $firstFailing")
        println("Difference: ${firstFailing.subtract(lastWorking)}")
        println("Total iterations: $iterations")

        // Do a detailed comparison
        println("\n=== Detailed Analysis ===")
        println("Last working ($lastWorking):")
        analyzeEncoding(lastWorking)

        println("\nFirst failing ($firstFailing):")
        analyzeEncoding(firstFailing)
    }

    private fun testValue(value: BigInteger): Boolean {
        return try {
            val encoded = WSPRMessage.encode(value)
            val decoded = encoded.decode()
            decoded == value
        } catch (e: Exception) {
            false
        }
    }

    private fun analyzeEncoding(value: BigInteger) {
        try {
            val encoded = WSPRMessage.encode(value)
            println("  Encoded successfully")
            println("  Prefix: ${encoded.prefix.value}")
            println("  Callsign: ${encoded.callsign1.value}${encoded.callsign2.value}${encoded.callsign3.value}${encoded.callsign4.value}${encoded.callsign5.value}")
            println("  Grid: ${encoded.grid1.value}${encoded.grid2.value}${encoded.grid3.value}${encoded.grid4.value}")
            println("  Power: ${encoded.power.value}")

            val decoded = encoded.decode()
            println("  Decoded: $decoded")
            println("  Match: ${decoded == value}")

            if (decoded != value) {
                println("  ERROR: Mismatch!")
                println("    Expected: $value")
                println("    Got:      $decoded")
                println("    Difference: ${value.subtract(decoded)}")
            }
        } catch (e: Exception) {
            println("  Exception: ${e.message}")
            e.printStackTrace()
        }
    }

    @Test
    fun testWSPRMessageSizeVerification() {
        println("\n=== Verifying WSPRMessage.size() ===")

        val size = WSPRMessage.size()
        println("WSPRMessage.size() = $size")

        val lastWorking = BigInteger.valueOf(1340027206041599L)
        val firstFailing = BigInteger.valueOf(1340027206041600L)

        println("Last working value:  $lastWorking")
        println("First failing value: $firstFailing")
        println("Size equals first failing? ${size == firstFailing}")
        println("Size equals last working + 1? ${size == lastWorking.add(BigInteger.ONE)}")

        // The maximum encodable value should be size - 1
        val expectedMax = size.subtract(BigInteger.ONE)
        println("\nExpected max (size - 1): $expectedMax")
        println("Matches last working? ${expectedMax == lastWorking}")
    }

    @Test
    fun testWSPRMessageSequenceDebug() {
        println("\n=== Debugging WSPRMessageSequence ===")

        val messageSize = WSPRMessage.size()
        println("Single message size: $messageSize")

        // Test a simple value that should fit in 1 message
        val testValue = BigInteger.valueOf(100)
        println("\nTesting value: $testValue")

        val encoded = WSPRMessageSequence.encode(testValue)

        println("Encoded into ${encoded.messages.size} message(s)")

        // Show each message
        encoded.messages.forEachIndexed { index, message ->
            println("  Message $index: ${message.decode()}")
        }

        val decoded = encoded.decode()
        println("Decoded result: $decoded")
        println("Match: ${decoded == testValue}")

        assertEquals(testValue, decoded, "Simple value should round-trip")

        // Now test the boundary value
        println("\n=== Testing boundary value ===")
        val boundaryValue = messageSize.subtract(BigInteger.ONE)
        println("Boundary value (messageSize - 1): $boundaryValue")

        val encodedBoundary = WSPRMessageSequence.encode(boundaryValue)
        println("Encoded into ${encodedBoundary.messages.size} message(s)")

        encodedBoundary.messages.forEachIndexed { index, message ->
            println("  Message $index decoded: ${message.decode()}")
        }

        val decodedBoundary = encodedBoundary.decode()
        println("Decoded result: $decodedBoundary")
        println("Match: ${decodedBoundary == boundaryValue}")

        assertEquals(boundaryValue, decodedBoundary, "Boundary value should round-trip")
    }

    @Test
    fun testWSPRMessageSequenceEncodingStep() {
        println("\n=== Step-by-step Encoding ===")

        val messageSize = WSPRMessage.size()
        println("messageSize: $messageSize")

        val testValue = messageSize.subtract(BigInteger.ONE)
        println("testValue: $testValue (messageSize - 1)")
        println("Should fit in: 1 message")
        println()

        // Manually trace the encode logic
        var remaining = testValue
        var iteration = 0

        println("Starting encode loop:")
        do {
            iteration++
            val value = remaining.mod(messageSize)
            println("  Iteration $iteration:")
            println("    remaining: $remaining")
            println("    value (mod messageSize): $value")
            println("    value < messageSize? ${value < messageSize}")

            try {
                val message = WSPRMessage.encode(value)
                println("    ✓ Encoded message successfully")
            } catch (e: Exception) {
                println("    ✗ Failed to encode: ${e.message}")
            }

            remaining = remaining.divide(messageSize)
            println("    new remaining: $remaining")
            println("    continuing? ${remaining > BigInteger.ZERO}")
            println()

        } while (remaining > BigInteger.ZERO)

        println("Total iterations: $iteration")
    }

    @Test
    fun testWSPRMessageSequenceVerySimple() {
        println("\n=== Very Simple WSPRMessageSequence Test ===")

        val messageSize = WSPRMessage.size()
        println("WSPRMessage.size(): $messageSize")

        // Test encoding zero
        println("\n--- Testing ZERO ---")
        val zero = BigInteger.ZERO
        val encodedZero = WSPRMessageSequence.encode(zero)
        println("Encoded 0 into ${encodedZero.messages.size} message(s)")

        encodedZero.messages.forEachIndexed { i, msg ->
            val decoded = msg.decode()
            println("  Message $i decoded: $decoded")
        }

        val decodedZero = encodedZero.decode()
        println("Final decoded value: $decodedZero")
        println("Match? ${decodedZero == zero}")

        // Test encoding one
        println("\n--- Testing ONE ---")
        val one = BigInteger.ONE
        val encodedOne = WSPRMessageSequence.encode(one)
        println("Encoded 1 into ${encodedOne.messages.size} message(s)")

        encodedOne.messages.forEachIndexed { i, msg ->
            val decoded = msg.decode()
            println("  Message $i decoded: $decoded")
        }

        val decodedOne = encodedOne.decode()
        println("Final decoded value: $decodedOne")
        println("Match? ${decodedOne == one}")

        // Test encoding 100
        println("\n--- Testing 100 ---")
        val hundred = BigInteger.valueOf(100)
        val encodedHundred = WSPRMessageSequence.encode(hundred)
        println("Encoded 100 into ${encodedHundred.messages.size} message(s)")

        encodedHundred.messages.forEachIndexed { i, msg ->
            val decoded = msg.decode()
            println("  Message $i decoded: $decoded")
        }

        val decodedHundred = encodedHundred.decode()
        println("Final decoded value: $decodedHundred")
        println("Match? ${decodedHundred == hundred}")
    }

    @Test
    fun testWSPRMessageSequenceBoundary() {
        println("\n=== WSPRMessageSequence Boundary Test ===")

        val messageSize = WSPRMessage.size()
        println("WSPRMessage.size(): $messageSize")

        // Test the max value for a single message
        val maxSingleMessage = messageSize.subtract(BigInteger.ONE)
        println("\n--- Testing max single message value ---")
        println("Value: $maxSingleMessage (messageSize - 1)")

        val encoded = WSPRMessageSequence.encode(maxSingleMessage)
        println("Encoded into ${encoded.messages.size} message(s)")

        encoded.messages.forEachIndexed { i, msg ->
            val decoded = msg.decode()
            println("  Message $i decoded: $decoded")
        }

        val decoded = encoded.decode()
        println("Final decoded value: $decoded")
        println("Expected: $maxSingleMessage")
        println("Match? ${decoded == maxSingleMessage}")

        assertEquals(maxSingleMessage, decoded, "Max single message value should round-trip")

        // Test the min value for two messages
        println("\n--- Testing min two message value ---")
        val minTwoMessages = messageSize
        println("Value: $minTwoMessages (messageSize)")

        val encoded2 = WSPRMessageSequence.encode(minTwoMessages)
        println("Encoded into ${encoded2.messages.size} message(s)")

        encoded2.messages.forEachIndexed { i, msg ->
            val decoded2 = msg.decode()
            println("  Message $i decoded: $decoded2")
        }

        val decoded2 = encoded2.decode()
        println("Final decoded value: $decoded2")
        println("Expected: $minTwoMessages")
        println("Match? ${decoded2 == minTwoMessages}")

        assertEquals(minTwoMessages, decoded2, "Min two message value should round-trip")
    }
}