package org.operatorfoundation.Codex

import org.operatorfoundation.codex.Decoder
import org.operatorfoundation.codex.Encoder
import org.operatorfoundation.codex.symbols.Number
import org.operatorfoundation.codex.symbols.*
import java.math.BigInteger

/**
 * WSPRCodex provides encoding and decoding of arbitrary byte data as WSPR messages.
 *
 * This class uses the Codex symbol system to encode binary data into the standard
 * WSPR message format (callsign, grid square, power level). The encoding is reversible,
 * allowing data to be transmitted via WSPR and recovered on the receiving end.
 *
 * WSPR Message Format:
 * - Callsign: 6 characters (letters, numbers, spaces)
 * - Grid Square: 4 characters (letters and numbers for Maidenhead locator)
 * - Power: 2 characters (power level in dBm: 0, 3, 7, 10, 13... 60)
 *
 * Example Usage:
 * ```kotlin
 * val codex = WSPRCodex()
 * val plaintext = "Hello WSPR!"
 * val encrypted = encryptData(plaintext) // External encryption
 *
 * // Encode to WSPR message
 * val wsprMessage = codex.encode(encrypted)
 * println("${wsprMessage.callsign} ${wsprMessage.gridSquare} ${wsprMessage.powerDbm}")
 *
 * // Decode back to original data
 * val decoded = codex.decode(wsprMessage)
 * val decrypted = decryptData(decoded) // External decryption
 * ```
 */
class WSPRCodex
{
    companion object
    {
        /**
         * WSPR symbol configuration matching the standard WSPR message format.
         *
         * Symbol breakdown:
         * - Required('Q'): Fixed prefix (1 byte, size=1, contributes 0 bits)
         * - CallLetterNumber (5x): Callsign characters (A-Z, 0-9 = 36 values each)
         * - GridLetter (2x): First two grid characters (A-R = 18 values each)
         * - Number (2x): Last two grid characters (0-9 = 10 values each)
         * - Power: Power level (19 discrete values: 0, 3, 7, 10... 60 dBm)
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
         * Calculates the maximum number of bytes that can be encoded in a single WSPR message.
         *
         * This is determined by the product of all symbol sizes (excluding size=1 symbols):
         * Capacity = log2(36^6 × 18^2 × 10^2 × 19) / 8 bytes
         *
         * @return Maximum payload size in bytes
         */
        fun getMaxPayloadBytes(): Int
        {
            // Calculate total capacity in bits
            val symbolSizes = WSPR_SYMBOLS.map { it.size().toBigInteger() }
            val totalCapacity = symbolSizes.fold(BigInteger.ONE) { acc, size -> acc * size }

            // Convert to bits (log2 of total capacity)
            val capacityBits = totalCapacity.bitLength() - 1

            // Convert to bytes (divide by 8)
            return capacityBits / 8
        }

        /**
         * Gets the symbol configuration used for WSPR encoding.
         * Useful for testing and capacity calculations.
         *
         * @return List of symbols defining the WSPR message structure
         */
        fun getSymbolList(): List<Symbol> = WSPR_SYMBOLS
    }

    // Encoder and decoder instances configured with WSPR symbols
    private val encoder = Encoder(WSPR_SYMBOLS)
    private val decoder = Decoder(WSPR_SYMBOLS)

    fun encode(data: ByteArray): WSPRDataMessage
    {
        // TODO: Not implemented
    }

    fun decode(message: WSPRDataMessage): ByteArray
    {
        // TODO: Not implemented
    }
}

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