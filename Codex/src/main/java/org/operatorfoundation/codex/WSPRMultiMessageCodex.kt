package org.operatorfoundation.codex

import java.math.BigInteger
import kotlin.random.Random

/**
 * WSPRMultiMessageCodex encodes and decodes data across multiple WSPR messages.
 *
 * - Splitting large data into multiple WSPR message chunks
 * - Adding framing metadata (message ID, sequence numbers, chunk counts)
 * - Reassembling chunks back into original data
 * - Automatic mode selection (basic vs extended) based on data size
 *
 * Two modes are supported:
 * - **Basic Mode**: Up to 16 chunks (80 bytes payload, ~50 chars encrypted text)
 * - **Extended Mode**: Up to 256 chunks (1280 bytes payload, ~800+ chars encrypted text)
 *
 * Frame Structure:
 * - Basic Mode: [MessageID: 1][Metadata: 1][Payload: 5] = 7 bytes per chunk
 * - Extended Mode: [MessageID: 1][SeqNum: 1][Total: 1][Payload: 4] = 7 bytes per chunk
 *
 * Example Usage:
 * ```kotlin
 * val codec = WSPRMultiMessageCodex()
 * val encrypted = encryptData("Hello World!") // 40+ bytes with encryption overhead
 *
 * // Encode to multiple WSPR messages
 * val messages = codec.encode(encrypted, messageId = 42)
 * // Returns: List of 8 WSPRDataMessage objects
 *
 * // Transmit each message via radio...
 * messages.forEach { transmit(it) }
 *
 * // Later, decode received messages
 * val decoded = codec.decode(messages)
 * val plaintext = decryptData(decoded)
 * ```
 */
class WSPRMultiMessageCodex
{
    // Use encoder/decoder directly instead of going through WSPRCodex
    private val encoder = Encoder(WSPRCodex.getSymbolList())
    private val decoder = Decoder(WSPRCodex.getSymbolList())

    companion object
    {
        // Basic mode constants (1-byte metadata encoding)
        private const val BASIC_MODE_MAX_CHUNKS = 16
        private const val BASIC_MODE_PAYLOAD_BYTES = 4  // Changed from 5 to 4
        private const val BASIC_MODE_MAX_PAYLOAD = BASIC_MODE_MAX_CHUNKS * BASIC_MODE_PAYLOAD_BYTES // 64 bytes

        // Extended mode constants (2-byte metadata encoding)
        private const val EXTENDED_MODE_MAX_CHUNKS = 256
        private const val EXTENDED_MODE_PAYLOAD_BYTES = 3  // Changed from 4 to 3
        private const val EXTENDED_MODE_MAX_PAYLOAD = EXTENDED_MODE_MAX_CHUNKS * EXTENDED_MODE_PAYLOAD_BYTES // 768 bytes

        /**
         * Generates a random message ID for encoding to help distinguish different multi-message transmissions.
         *
         * @return Random byte value (0-255) for use as message ID
         */
        fun generateRandomMessageId(): Byte = Random.nextBytes(1)[0]
    }

    /**
     * Encodes data into multiple WSPR messages with internal mode selection.
     *
     * The codec chooses:
     * - Basic mode for data ≤ 80 bytes (up to 16 chunks, 5 bytes per chunk)
     * - Extended mode for data > 80 bytes (up to 256 chunks, 4 bytes per chunk)
     *
     * @param data Binary data to encode (typically encrypted message bytes)
     * @param messageId Unique identifier for this message sequence (0-255)
     * @return List of WSPR messages containing the chunked data
     * @throws WSPRMultiMessageException if data exceeds maximum capacity
     */
    fun encode(data: ByteArray, messageId: Byte = generateRandomMessageId()): List<WSPRDataMessage>
    {
        // Validate data size
        if (data.isEmpty())
        {
            throw WSPRMultiMessageException("Cannot encode empty data")
        }

        if (data.size > EXTENDED_MODE_MAX_PAYLOAD)
        {
            throw WSPRMultiMessageException(
                "Data size ${data.size} bytes exceeds maximum capacity of $EXTENDED_MODE_MAX_PAYLOAD bytes. " +
                        "Consider compressing data or splitting into multiple independent messages."
            )
        }

        // Select encoding mode based on data size
        return if (data.size <= BASIC_MODE_MAX_PAYLOAD)
        {
            encodeBasicMode(data, messageId)
        }
        else
        {
            encodeExtendedMode(data, messageId)
        }
    }

    /**
     * Decodes multiple WSPR messages back into the original data.
     *
     * This method:
     * - Automatically detects encoding mode (basic vs extended)
     * - Validates that all chunks belong to the same message
     * - Verifies sequence completeness (no missing chunks)
     * - Reassembles chunks in correct order
     *
     * @param messages List of WSPR messages to decode (order doesn't matter)
     * @return Original data that was encoded
     * @throws WSPRMultiMessageException if messages are invalid or incomplete
     */
    fun decode(messages: List<WSPRDataMessage>): ByteArray
    {
        if (messages.isEmpty())
        {
            throw WSPRMultiMessageException("Cannot decode empty message list")
        }

        // Decode all messages to get raw chunk data
        val rawChunks = messages.map { message ->
            val encodedSymbols = WSPRMessageConverter.convertMessageToEncodedSymbols(message)
            val decodedInteger = decoder.decode(encodedSymbols)

            // Convert BigInteger to ByteArray
            val bytes = decodedInteger.toByteArray()

            when {
                // If we have exactly 6 bytes, use them
                bytes.size == 6 -> bytes

                // If we have 7 bytes with a leading zero (sign byte), skip it
                bytes.size == 7 && bytes[0] == 0x00.toByte() -> bytes.copyOfRange(1, 7)

                // If we have less than 6 bytes, pad with TRAILING zeros
                bytes.size < 6 -> bytes.copyOf(6)

                // Otherwise something's wrong
                else -> throw WSPRMultiMessageException("Unexpected chunk size: ${bytes.size} bytes")
            }
        }

        // Determine mode by looking at chunk structure
        val mode = detectEncodingMode(rawChunks)

        // Decode based on detected mode
        return when (mode)
        {
            EncodingMode.BASIC -> decodeBasicMode(rawChunks)
            EncodingMode.EXTENDED -> decodeExtendedMode(rawChunks)
        }
    }

    // ========== Basic Mode Implementation (16 chunks max, 5 bytes payload) ==========

    /**
     * Encodes data using basic mode (1-byte metadata, up to 16 chunks).
     *
     * Frame format: [MessageID: 1][Metadata: 1][Payload: 5]
     * Metadata byte: [SeqNum: 4 bits][TotalChunks: 4 bits]
     */
    private fun encodeBasicMode(data: ByteArray, messageId: Byte): List<WSPRDataMessage>
    {
        val totalChunks = (data.size + BASIC_MODE_PAYLOAD_BYTES - 1) / BASIC_MODE_PAYLOAD_BYTES

        if (totalChunks > BASIC_MODE_MAX_CHUNKS)
        {
            throw WSPRMultiMessageException(
                "Basic mode supports max $BASIC_MODE_MAX_CHUNKS chunks, but $totalChunks needed"
            )
        }

        val wsprMessages = mutableListOf<WSPRDataMessage>()

        for (chunkIndex in 0 until totalChunks)
        {
            val startOffset = chunkIndex * BASIC_MODE_PAYLOAD_BYTES
            val endOffset = minOf(startOffset + BASIC_MODE_PAYLOAD_BYTES, data.size)

            // Build chunk frame: [MessageID][Metadata][Payload]
            val chunkData = ByteArray(6)  // Changed to 6 bytes

            // Byte 0: Message ID
            chunkData[0] = messageId

            // Byte 1: Metadata (sequence number in upper 4 bits, total chunks-1 in lower 4 bits)
            chunkData[1] = ((chunkIndex shl 4) or (totalChunks - 1)).toByte()

            // Bytes 2-5: Payload (pad with zeros if needed) - now 4 bytes
            data.copyInto(
                destination = chunkData,
                destinationOffset = 2,
                startIndex = startOffset,
                endIndex = endOffset
            )

            val dataAsInteger = BigInteger(1, chunkData)
            val encodedSymbols = encoder.encode(dataAsInteger)
            val wsprMessage = WSPRMessageConverter.parseEncodedSymbolsToMessage(encodedSymbols)
            wsprMessages.add(wsprMessage)
        }

        return wsprMessages
    }

    /**
     * Decodes data from basic mode chunks.
     */
    private fun decodeBasicMode(rawChunks: List<ByteArray>): ByteArray
    {
        // Parse all chunks
        val chunks = rawChunks.map { parseBasicModeChunk(it) }

        // Validate chunks
        validateChunks(chunks)

        // Sort by sequence number
        val sortedChunks = chunks.sortedBy { it.sequenceNumber }

        // Reassemble payload
        val result = ByteArray(sortedChunks.size * BASIC_MODE_PAYLOAD_BYTES)
        var resultOffset = 0

        sortedChunks.forEach { chunk ->
            chunk.payload.copyInto(result, resultOffset)
            resultOffset += chunk.payload.size
        }

        // Trim trailing zeros from last chunk padding
        val lastChunk = sortedChunks.last()
        val trailingZeros = lastChunk.payload.reversed().takeWhile { it == 0.toByte() }.count()
        val actualSize = result.size - trailingZeros

        return result.copyOf(actualSize)
    }

    /**
     * Parses a basic mode chunk from raw bytes.
     */
    private fun parseBasicModeChunk(rawBytes: ByteArray): WSPRChunk
    {
        if (rawBytes.size != 6) {
            throw WSPRMultiMessageException("Invalid basic mode chunk size: ${rawBytes.size}")
        }

        val messageId = rawBytes[0]
        val metadata = rawBytes[1].toInt() and 0xFF
        val sequenceNumber = (metadata shr 4) and 0x0F
        val totalChunks = (metadata and 0x0F) + 1

        val payload = rawBytes.copyOfRange(2, 6)  // Now 4 bytes

        return WSPRChunk(messageId, sequenceNumber, totalChunks, payload)
    }

    // ========== Extended Mode Implementation (256 chunks max, 4 bytes payload) ==========

    /**
     * Encodes data using extended mode (2-byte metadata, up to 256 chunks).
     *
     * Frame format: [MessageID: 1][SeqNum: 1][TotalChunks: 1][Payload: 4]
     */
    private fun encodeExtendedMode(data: ByteArray, messageId: Byte): List<WSPRDataMessage>
    {
        val totalChunks = (data.size + EXTENDED_MODE_PAYLOAD_BYTES - 1) / EXTENDED_MODE_PAYLOAD_BYTES

        if (totalChunks > EXTENDED_MODE_MAX_CHUNKS)
        {
            throw WSPRMultiMessageException(
                "Extended mode supports max $EXTENDED_MODE_MAX_CHUNKS chunks, but $totalChunks needed"
            )
        }

        val wsprMessages = mutableListOf<WSPRDataMessage>()

        for (chunkIndex in 0 until totalChunks)
        {
            val startOffset = chunkIndex * EXTENDED_MODE_PAYLOAD_BYTES
            val endOffset = minOf(startOffset + EXTENDED_MODE_PAYLOAD_BYTES, data.size)

            // Build chunk frame: [MessageID][SeqNum][Total][Payload]
            val chunkData = ByteArray(6)  // Changed to 6 bytes

            // Byte 0: Message ID
            chunkData[0] = messageId

            // Byte 1: Sequence number (0-255)
            chunkData[1] = chunkIndex.toByte()

            // Byte 2: Total chunks (1-256, stored as 0-255)
            chunkData[2] = (totalChunks - 1).toByte()

            // Bytes 3-5: Payload (pad with zeros if needed) - now 3 bytes
            data.copyInto(
                destination = chunkData,
                destinationOffset = 3,
                startIndex = startOffset,
                endIndex = endOffset
            )

            val dataAsInteger = BigInteger(1, chunkData)
            val encodedSymbols = encoder.encode(dataAsInteger)
            val wsprMessage = WSPRMessageConverter.parseEncodedSymbolsToMessage(encodedSymbols)
            wsprMessages.add(wsprMessage)
        }

        return wsprMessages
    }

    /**
     * Decodes data from extended mode chunks.
     */
    private fun decodeExtendedMode(rawChunks: List<ByteArray>): ByteArray
    {
        // Parse all chunks
        val chunks = rawChunks.map { parseExtendedModeChunk(it) }

        // Validate chunks
        validateChunks(chunks)

        // Sort by sequence number
        val sortedChunks = chunks.sortedBy { it.sequenceNumber }

        // Reassemble payload
        val result = ByteArray(sortedChunks.size * EXTENDED_MODE_PAYLOAD_BYTES)
        var resultOffset = 0

        sortedChunks.forEach { chunk ->
            chunk.payload.copyInto(result, resultOffset)
            resultOffset += chunk.payload.size
        }

        // Trim trailing zeros from last chunk padding
        val lastChunk = sortedChunks.last()
        val trailingZeros = lastChunk.payload.reversed().takeWhile { it == 0.toByte() }.count()
        val actualSize = result.size - trailingZeros

        return result.copyOf(actualSize)
    }

    /**
     * Parses an extended mode chunk from raw bytes.
     */
    private fun parseExtendedModeChunk(rawBytes: ByteArray): WSPRChunk
    {
        if (rawBytes.size != 6) {
            throw WSPRMultiMessageException("Invalid extended mode chunk size: ${rawBytes.size}")
        }

        val messageId = rawBytes[0]
        val sequenceNumber = rawBytes[1].toInt() and 0xFF
        val totalChunks = (rawBytes[2].toInt() and 0xFF) + 1

        val payload = rawBytes.copyOfRange(3, 6)  // Now 3 bytes

        return WSPRChunk(messageId, sequenceNumber, totalChunks, payload)
    }

    // ========== Helper Methods ==========

    /**
     * Detects encoding mode by examining chunk structure.
     */
    private fun detectEncodingMode(rawChunks: List<ByteArray>): EncodingMode
    {
        if (rawChunks.isEmpty()) {
            throw WSPRMultiMessageException("No chunks to analyze")
        }

        val firstChunk = rawChunks.first()
        val totalChunkCount = rawChunks.size

        // All chunks should be 6 bytes after padding
        if (firstChunk.size != 6) {
            throw WSPRMultiMessageException("Invalid chunk size: ${firstChunk.size}")
        }

        // Try basic mode interpretation
        val metadata = firstChunk[1].toInt() and 0xFF
        val totalBasic = (metadata and 0x0F) + 1

        // Try extended mode interpretation
        val totalExtended = (firstChunk[2].toInt() and 0xFF) + 1

        // If chunk count matches basic mode and is within limits, use basic
        if (totalChunkCount <= BASIC_MODE_MAX_CHUNKS && totalChunkCount == totalBasic) {
            return EncodingMode.BASIC
        }

        // If chunk count matches extended mode, use it
        if (totalChunkCount == totalExtended) {
            return EncodingMode.EXTENDED
        }

        // Default based on chunk count
        return if (totalChunkCount <= BASIC_MODE_MAX_CHUNKS) {
            EncodingMode.BASIC
        } else {
            EncodingMode.EXTENDED
        }
    }

    /**
     * Validates that all chunks are consistent and complete.
     */
    private fun validateChunks(chunks: List<WSPRChunk>)
    {
        if (chunks.isEmpty())
        {
            throw WSPRMultiMessageException("No chunks to decode")
        }

        // All chunks must have same message ID
        val messageId = chunks.first().messageId
        if (chunks.any { it.messageId != messageId })
        {
            throw WSPRMultiMessageException(
                "Chunks have mismatched message IDs: expected $messageId"
            )
        }

        // All chunks must agree on total count
        val totalChunks = chunks.first().totalChunks
        if (chunks.any { it.totalChunks != totalChunks })
        {
            throw WSPRMultiMessageException(
                "Chunks have mismatched total counts: expected $totalChunks"
            )
        }

        // Must have exactly the expected number of chunks
        if (chunks.size != totalChunks)
        {
            throw WSPRMultiMessageException(
                "Expected $totalChunks chunks but received ${chunks.size}"
            )
        }

        // Sequence numbers must be 0..(totalChunks-1) with no gaps
        val sequences = chunks.map { it.sequenceNumber }.sorted()
        val expectedSequences = (0 until totalChunks).toList()
        if (sequences != expectedSequences)
        {
            throw WSPRMultiMessageException(
                "Missing or duplicate chunks. Expected: $expectedSequences, Got: $sequences"
            )
        }
    }

    // ========== Internal Data Structures ==========

    /**
     * Internal representation of a decoded chunk.
     */
    private data class WSPRChunk(
        val messageId: Byte,
        val sequenceNumber: Int,
        val totalChunks: Int,
        val payload: ByteArray
    )
    {
        override fun equals(other: Any?): Boolean
        {
            if (this === other) return true
            if (other !is WSPRChunk) return false

            if (messageId != other.messageId) return false
            if (sequenceNumber != other.sequenceNumber) return false
            if (totalChunks != other.totalChunks) return false
            if (!payload.contentEquals(other.payload)) return false

            return true
        }

        override fun hashCode(): Int
        {
            var result = messageId.toInt()
            result = 31 * result + sequenceNumber
            result = 31 * result + totalChunks
            result = 31 * result + payload.contentHashCode()
            return result
        }
    }

    /**
     * Encoding mode selection.
     */
    private enum class EncodingMode
    {
        BASIC,      // Up to 16 chunks, 5 bytes per chunk
        EXTENDED    // Up to 256 chunks, 4 bytes per chunk
    }
}

/**
 * Exception thrown by WSPRMultiMessageCodex for encoding/decoding errors.
 */
class WSPRMultiMessageException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)