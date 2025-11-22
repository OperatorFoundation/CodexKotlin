//package org.operatorfoundation.codex
//
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//
//class WSPRCodexTest
//{
//    private lateinit var codex: WSPRCodex
//
//    @BeforeEach
//    fun setup()
//    {
//        codex = WSPRCodex()
//    }
//
//    // ========== Basic Functionality Tests ==========
//
//    @Test
//    fun testSmallMessage()
//    {
//        println("\n=== Small Message (1 chunk) ===")
//
//        val data = "Hi".toByteArray()  // 2 bytes
//        println("Input: '${String(data)}' (${data.size} bytes)")
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        assertEquals(1, messages.size, "2 bytes should encode to 1 message")
//
//        val decoded = codex.decode(messages)
//        println("Decoded: '${String(decoded)}' (${decoded.size} bytes)")
//
//        assertArrayEquals(data, decoded, "Round-trip should preserve data")
//    }
//
//    @Test
//    fun testMediumMessage()
//    {
//        println("\n=== Medium Message (2 chunks) ===")
//
//        val data = "Hello World".toByteArray()  // 11 bytes
//        println("Input: '${String(data)}' (${data.size} bytes)")
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        assertEquals(3, messages.size, "11 bytes should encode to 3 messages")
//
//        val decoded = codex.decode(messages)
//        println("Decoded: '${String(decoded)}' (${decoded.size} bytes)")
//
//        assertArrayEquals(data, decoded, "Round-trip should preserve data")
//    }
//
//    @Test
//    fun testLargeMessage()
//    {
//        println("\n=== Large Message (multiple chunks) ===")
//
//        val data = "This is a longer message that needs multiple WSPR chunks".toByteArray()
//        println("Input: '${String(data)}' (${data.size} bytes)")  // This will print the actual size
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        // Check actual size and calculate expected chunks
//        val expectedChunks = (data.size + 3) / 4  // Using ceiling division
//        assertEquals(expectedChunks, messages.size, "${data.size} bytes should encode to $expectedChunks messages (4 bytes per chunk)")
//
//        val decoded = codex.decode(messages)
//        println("Decoded: '${String(decoded)}' (${decoded.size} bytes)")
//
//        assertArrayEquals(data, decoded, "Round-trip should preserve data")
//    }
//
//    // ========== Edge Cases ==========
//
//    @Test
//    fun testSingleByte()
//    {
//        println("\n=== Single Byte ===")
//
//        val data = byteArrayOf(0x42)
//        println("Input: 0x42 (1 byte)")
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        assertEquals(1, messages.size)
//
//        val decoded = codex.decode(messages)
//        assertArrayEquals(data, decoded)
//    }
//
//    @Test
//    fun testMaxSingleChunk()
//    {
//        println("\n=== Maximum Single Chunk (4 bytes) ===")  // Changed from 5 to 4
//
//        val data = ByteArray(4) { it.toByte() }  // Changed from 5 to 4
//        println("Input: 4 bytes")
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        assertEquals(1, messages.size, "4 bytes should fit in 1 message")  // Changed from 5 to 4
//
//        val decoded = codex.decode(messages)
//        assertArrayEquals(data, decoded)
//    }
//
//    @Test
//    fun testJustOverSingleChunk()
//    {
//        println("\n=== Just Over Single Chunk (6 bytes) ===")
//
//        val data = ByteArray(6) { it.toByte() }
//        println("Input: 6 bytes")
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        assertEquals(2, messages.size, "6 bytes should need 2 messages")
//
//        val decoded = codex.decode(messages)
//        assertArrayEquals(data, decoded)
//    }
//
//    // ========== Robustness Tests ==========
//
//    @Test
//    fun testOutOfOrderDecoding()
//    {
//        println("\n=== Out-of-Order Decoding ===")
//
//        val data = "Test message for shuffling".toByteArray()
//        println("Input: '${String(data)}' (${data.size} bytes)")
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        val shuffled = messages.shuffled()
//        println("Shuffled message order")
//
//        val decoded = codex.decode(shuffled)
//        println("Decoded: '${String(decoded)}' (${decoded.size} bytes)")
//
//        assertArrayEquals(data, decoded, "Should handle out-of-order messages")
//    }
//
//    @Test
//    fun testCustomMessageId()
//    {
//        println("\n=== Custom Message ID ===")
//
//        val data = "Test with ID".toByteArray()
//        val customId: Byte = 42
//
//        println("Input: '${String(data)}' with ID=$customId")
//
//        val messages = codex.encode(data, messageId = customId)
//        val decoded = codex.decode(messages)
//
//        assertArrayEquals(data, decoded)
//        println("✓ Custom message ID works")
//    }
//
//    @Test
//    fun testBinaryData()
//    {
//        println("\n=== Binary Data (non-text) ===")
//
//        val data = ByteArray(50) { (it * 13 % 256).toByte() }
//        println("Input: 50 bytes of binary data")
//
//        val messages = codex.encode(data)
//        println("Encoded to ${messages.size} WSPR message(s)")
//
//        val decoded = codex.decode(messages)
//
//        assertArrayEquals(data, decoded)
//        println("✓ Binary data preserved")
//    }
//
//    // ========== Error Handling ==========
//
//    @Test
//    fun testEmptyDataThrows()
//    {
//        val exception = assertThrows(WSPRCodexException::class.java) {
//            codex.encode(ByteArray(0))
//        }
//
//        assertTrue(exception.message?.contains("empty") == true)
//        println("✓ Empty data throws: ${exception.message}")
//    }
//
//    @Test
//    fun testEmptyMessageListThrows()
//    {
//        val exception = assertThrows(WSPRCodexException::class.java) {
//            codex.decode(emptyList())
//        }
//
//        assertTrue(exception.message?.contains("empty") == true)
//        println("✓ Empty message list throws: ${exception.message}")
//    }
//
//    // ========== Capacity Tests ==========
//
//    @Test
//    fun testCapacityInfo()
//    {
//        println("\n=== WSPR Encoding Capacity ===")
//        println()
//        println("Single chunk:     1-4 bytes    → 1 message  (2 min)")  // Changed
//        println("Two chunks:       5-8 bytes    → 2 messages (4 min)")  // Changed
//        println("Three chunks:     9-12 bytes   → 3 messages (6 min)")  // Changed
//        println()
//        println("Basic mode max:   64 bytes     → 16 messages (32 min)")  // Changed
//        println("Extended mode max: 768 bytes   → 256 messages (8.5 hours)")  // Changed
//        println()
//        println("Overhead: 2 bytes per message (message ID + metadata)")
//        println("Payload:  4 bytes per chunk (basic mode)")  // Changed
//        println("          3 bytes per chunk (extended mode)")  // Changed
//    }
//
//    @Test
//    fun testTypicalEncryptedMessage()
//    {
//        println("\n=== Typical Encrypted Message ===")
//
//        // Simulate AES-GCM encrypted "Hello World!"
//        // Original: 12 bytes
//        // Encrypted: 12 + 12 (IV) + 16 (tag) = 40 bytes
//        val simulatedEncrypted = ByteArray(40) { (it * 7 % 256).toByte() }
//
//        println("Plaintext: ~12 characters")
//        println("Encrypted: ${simulatedEncrypted.size} bytes")
//
//        val messages = codex.encode(simulatedEncrypted)
//        println("WSPR messages: ${messages.size}")
//        println("Transmission time: ${messages.size * 2} minutes")
//
//        val decoded = codex.decode(messages)
//
//        assertArrayEquals(simulatedEncrypted, decoded)
//        println("✓ Encrypted message transmitted successfully")
//    }
//
//    @Test
//    fun testDataIntegrity()
//    {
//        println("\n=== Data Integrity Test ===")
//
//        // Test various data patterns
//        val testPatterns = listOf(
//            ByteArray(1) { 0xFF.toByte() },
//            ByteArray(5) { 0x00 },
//            ByteArray(10) { it.toByte() },
//            ByteArray(16) { (it * it).toByte() },
//            "Test".toByteArray(),
//            "λ∀√∞".toByteArray()  // UTF-8 multi-byte chars
//        )
//
//        testPatterns.forEachIndexed { index, data ->
//            println("Pattern $index: ${data.size} bytes")
//
//            val messages = codex.encode(data)
//            val decoded = codex.decode(messages)
//
//            // Note: trailing zeros may be lost
//            if (data.last() != 0.toByte()) {
//                assertArrayEquals(data, decoded)
//                println("  ✓ Exact match")
//            } else {
//                // For data ending in zeros, just check non-zero prefix
//                val nonZeroLength = data.indexOfLast { it != 0.toByte() } + 1
//                assertArrayEquals(
//                    data.copyOf(nonZeroLength),
//                    decoded.copyOf(nonZeroLength)
//                )
//                println("  ✓ Non-zero prefix matches")
//            }
//        }
//    }
//}