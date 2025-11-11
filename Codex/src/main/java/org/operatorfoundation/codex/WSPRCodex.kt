package org.operatorfoundation.codex

import org.operatorfoundation.codex.symbols.Number
import org.operatorfoundation.codex.symbols.*

/**
 * WSPRCodex provides encoding and decoding of arbitrary byte data as WSPR messages.
 *
 * This class uses the Codex symbol system to encode binary data into the standard
 * WSPR message format (callsign, grid square, power level). The encoding is reversible,
 * allowing data to be transmitted via WSPR and recovered on the receiving end.
 *
 * All data is encoded using the multi-message codec for consistency and robustness.
 * Small data (≤5 bytes) requires only 1 WSPR message. Larger data automatically
 * chunks across multiple messages.
 *
 * WSPR Message Format:
 * - Callsign: 6 characters (letters, numbers, spaces)
 * - Grid Square: 4 characters (letters and numbers for Maidenhead locator)
 * - Power: 2 digits (power level in dBm: 0, 3, 7, 10, 13... 60)
 *
 * Example Usage:
 * ```kotlin
 * val codex = WSPRCodex()
 * val plaintext = "Hello WSPR!"
 * val encrypted = encryptData(plaintext) // External encryption
 *
 * // Encode to WSPR messages (automatically handles chunking)
 * val messages = codex.encode(encrypted)
 * println("Encoded to ${messages.size} WSPR message(s)")
 *
 * // Transmit each message via radio
 * messages.forEach { message ->
 *     transmit(message)
 * }
 *
 * // Decode back to original data (chunks can be in any order)
 * val decoded = codex.decode(messages)
 * val decrypted = decryptData(decoded) // External decryption
 * ```
 *
 * Note: Trailing zeros in data may be lost during encoding/decoding.
 * This is typically not an issue for encrypted data.
 */
class WSPRCodex
{
    companion object
    {
        /**
         * WSPR symbol configuration matching the standard WSPR message format.
         */
        private val WSPR_SYMBOLS: List<Symbol> = listOf<Symbol>(
            Required('Q'.code.toByte()),  // Fixed prefix
            CallLetterNumber(),            // Callsign char 1
            CallLetterNumber(),            // Callsign char 2
            CallLetterNumber(),            // Callsign char 3
            CallLetterNumber(),            // Callsign char 4
            CallLetterNumber(),            // Callsign char 5
            CallLetterNumber(),            // Callsign char 6 (often space)
            GridLetter(),                  // Grid char 1
            GridLetter(),                  // Grid char 2
            Number(),                      // Grid char 3
            Number(),                      // Grid char 4
            Power()                        // Power level
        )

        /**
         * Gets the symbol configuration used for WSPR encoding.
         *
         * @return List of symbols defining the WSPR message structure
         */
        fun getSymbolList(): List<Symbol> = WSPR_SYMBOLS

        /**
         * Generates a random message ID for multi-message encoding.
         *
         * @return Random byte value (0-255)
         */
        fun generateRandomMessageId(): Byte = kotlin.random.Random.nextBytes(1)[0]
    }

    private val multiMessageCodec = WSPRMultiMessageCodex()

    /**
     * Encodes a byte array as one or more WSPR messages.
     *
     * Data capacity per message chunk:
     * - ≤5 bytes: 1 WSPR message
     * - 6-10 bytes: 2 WSPR messages
     * - 11-15 bytes: 3 WSPR messages
     * - etc. (up to 1024 bytes total)
     *
     * @param data Binary data to encode (e.g., encrypted message bytes)
     * @param messageId Optional message ID for tracking (auto-generated if null)
     * @return List of WSPR messages containing the encoded data
     * @throws WSPRCodexException if data is empty or exceeds maximum capacity
     */
    fun encode(data: ByteArray, messageId: Byte? = null): List<WSPRDataMessage>
    {
        if (data.isEmpty())
        {
            throw WSPRCodexException("Cannot encode empty data")
        }

        val actualMessageId = messageId ?: generateRandomMessageId()
        return multiMessageCodec.encode(data, actualMessageId)
    }

    /**
     * Decodes one or more WSPR messages back to the original byte array.
     *
     * Messages can be provided in any order - the decoder automatically
     * handles sequencing and reassembly. All chunks from the same message
     * ID must be provided for successful decoding.
     *
     * @param messages List of WSPR messages to decode
     * @return Original byte array that was encoded
     * @throws WSPRCodexException if messages are invalid, empty, or incomplete
     */
    fun decode(messages: List<WSPRDataMessage>): ByteArray
    {
        if (messages.isEmpty())
        {
            throw WSPRCodexException("Cannot decode empty message list")
        }

        return multiMessageCodec.decode(messages)
    }
}

/**
 * Exception thrown by WSPRCodex for encoding/decoding errors.
 */
class WSPRCodexException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)


/**
 * Data class representing a WSPR message with encoded data.
 *
 * This structure mirrors the format used by AudioCoder's CJarInterface
 * for WSPR encoding and can be directly passed to audio generation methods.
 *
 * @property callsign Amateur radio callsign (6 characters, letters/numbers/spaces)
 * @property gridSquare Maidenhead grid locator (4 characters, e.g., "FN31")
 * @property powerDbm Transmitter power in dBm (0, 3, 7, 10, 13... 60)
 */
data class WSPRDataMessage(
    val callsign: String,
    val gridSquare: String,
    val powerDbm: Int
)
{
    /**
     * Formats the WSPR message in standard display format.
     * This is the format typically shown to operators and used in logs.
     *
     * Example: "K1ABC FN31 23"
     */
    override fun toString(): String = "$callsign $gridSquare $powerDbm"

    /**
     * Validates that the message fields contain valid WSPR data.
     *
     * @return true if all fields are valid, false otherwise
     */
    fun isValid(): Boolean
    {
        return callsign.length <= 6 &&
                callsign.all { it.isLetterOrDigit() || it == ' ' } &&
                gridSquare.length == 4 &&
                powerDbm in 0..60
    }
}
