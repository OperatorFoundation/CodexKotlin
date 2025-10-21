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
    fun testMaxPayload()
    {
        // Verify the calculated maximum payload is reasonable
        val maxBytes = WSPRCodex.getMaxPayloadBytes()

        println("Maximum WSPR payload capacity: $maxBytes bytes")

        // Should be around 28 bytes based on symbol sizes
        assertTrue(maxBytes in 20..35, "Expected capacity around 28 bytes, got $maxBytes")
    }

    @Test
    fun testActualCapacity() {
        // Try encoding increasingly large byte arrays until we find the limit
        var workingSize = 0

        for (size in 1..50) {
            val testData = ByteArray(size) { 0xFF.toByte() }
            try {
                codex.encode(testData)
                workingSize = size
                println("Size $size: SUCCESS")
            } catch (e: Exception) {
                println("Size $size: FAILED - ${e.message}")
                break
            }
        }

        println("Actual working capacity: $workingSize bytes")
    }
}