package org.operatorfoundation.codex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.operatorfoundation.codex.WSPRCodex
import java.math.BigInteger

class WSPRCodexTest
{
    private lateinit var codex: WSPRCodex

    @BeforeEach
    fun setup()
    {
        codex = WSPRCodex()
    }

    @Test
    fun testCompleteWorkflow()
    {
        println("\n=== Complete WSPR Encoding Workflow ===")

        // Step 1: Create a short message
        val plaintext = "Hello"
        println("1. Plaintext: '$plaintext' (${plaintext.length} chars)")

        // Step 2: Simulate encryption
        val plaintextBytes = plaintext.toByteArray()
        println("2. Plaintext bytes: ${plaintextBytes.size} bytes")

        // Check capacity
        val maxCapacity = WSPRCodex.getMaxPayloadBytes()
        println("3. WSPR capacity: $maxCapacity bytes")
        assertTrue(plaintextBytes.size <= maxCapacity, "Message fits in capacity")

        // Step 3: Encode to WSPR message
        val wsprMessage = codex.encode(plaintextBytes)
        println("4. WSPR Message: $wsprMessage")
        println("   - Callsign: '${wsprMessage.callsign}'")
        println("   - Grid: '${wsprMessage.gridSquare}'")
        println("   - Power: ${wsprMessage.powerDbm} dBm")

        // Step 4: Verify message is valid
        assertTrue(wsprMessage.isValid(), "WSPR message should be valid")

        // Step 5: Decode back to bytes
        val decodedBytes = codex.decode(wsprMessage)
        println("5. Decoded bytes: ${decodedBytes.size} bytes")

        // Step 6: Convert back to text
        val decodedText = String(decodedBytes)
        println("6. Decoded text: '$decodedText'")

        // Verify round-trip
        assertEquals(plaintext, decodedText, "Round-trip should preserve original text")
        println("✓ Round-trip successful!\n")
    }

    @Test
    fun testMaxPayload()
    {
        // Verify the calculated maximum payload is reasonable
        val maxBytes = WSPRCodex.getMaxPayloadBytes()

        println("Maximum WSPR payload capacity: $maxBytes bytes")

        // WSPR symbol configuration provides 7 bytes of capacity
        assertEquals(7, maxBytes, "Expected exactly 7 bytes capacity")
    }

    @Test
    fun testActualCapacity() {
        // Try encoding increasingly large byte arrays until we find the limit
        var workingSize = 0

        for (size in 1..50)
        {
            val testData = ByteArray(size) { 0xFF.toByte() }
            try
            {
                codex.encode(testData)
                workingSize = size
                println("Size $size: SUCCESS")
            }
            catch (e: Exception)
            {
                println("Size $size: FAILED - ${e.message}")
                break
            }
        }

        println("Actual working capacity: $workingSize bytes")
    }
}