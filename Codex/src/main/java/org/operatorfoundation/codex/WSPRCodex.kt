package org.operatorfoundation.codex

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
            // DEBUG: Print out what symbols we have and their sizes
            println("=== Symbol Configuration Debug ===")
            WSPR_SYMBOLS.forEachIndexed { index, symbol ->
                println("Symbol $index: $symbol, size: ${symbol.size()}")
            }

            // Calculate the product of all symbol sizes
            // e.g. if we have symbols with sizes [36, 36, 18, 10, 19]
            // total capacity = 36 x 36 x 18 x 10 x 19 (total number of unique calculations)

            // Convert list of symbols to list of their sizes
            val symbolSizes = WSPR_SYMBOLS.map { it.size().toBigInteger() }
            // Multiply all  sizes together
            val totalCapacity = symbolSizes.fold(BigInteger.ONE) { acc, size -> acc * size}

            // The max integer we can encode is (totalCapacity - 1)
            val maxEncodableValue = totalCapacity - BigInteger.ONE

            // Convert maxEncodableValue to bytes so we know how many bytes we need (how many bytes  it takes to store this number)
            val byteArray = maxEncodableValue.toByteArray()

            // BigInteger sometimes adds an extra leading zero byte for the sign
            // Remove the extra byte if it exists
            return if (byteArray.isNotEmpty() && byteArray[0] == 0.toByte())
            {
                byteArray.size - 1
            }
            else
            {
                byteArray.size
            }

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

    /**
     * Encodes a byte array as a WSPR message.
     *
     * The data is converted to a large integer and distributed across the WSPR
     * message fields according to the symbol configuration. The encoding is
     * deterministic and reversible.
     *
     * @param data Binary data to encode (e.g., encrypted message bytes)
     * @return WSPRDataMessage containing callsign, grid square, and power level
     * @throws WSPRCodexException if data exceeds maximum capacity
     */
    fun encode(data: ByteArray): WSPRDataMessage
    {
        // Validate payload size
        if (data.size > getMaxPayloadBytes())
        {
            throw WSPRCodexException(
                "Data size ${data.size} bytes exceeds maximum capacity of ${getMaxPayloadBytes()} bytes. " +
                        "Consider splitting into multiple messages or reducing payload size."
            )
        }

        // Convert byte array to BigInt for encoding
        // Uses big-endian byte order
        val dataAsInteger = BigInteger(1, data) // 1 = positive

        // Encode integer across WSPR symbols
        val encodedSymbols = encoder.encode(dataAsInteger)

        // Parse encoded symbols into WSPR message fields
        return parseEncodedSymbolsToMessage(encodedSymbols)
    }


    /**
     * Decodes a WSPR message back to the original byte array.
     *
     * Reverses the encoding process by converting the WSPR message fields
     * back to symbol representations, then decoding to the original integer
     * and finally to the byte array.
     *
     * @param message WSPR message to decode
     * @return Original byte array that was encoded
     * @throws WSPRCodexException if message format is invalid
     */
    fun decode(message: WSPRDataMessage): ByteArray
    {
        // Convert message fields back to encoded symbol format
        val encodedSymbols = convertMessageToEncodedSymbols(message)

        // Decode symbols back to int
        val decodedInteger = decoder.decode(encodedSymbols)

        // Convert integer back to byte array
        return decodedInteger.toByteArray().let { bytes ->
            // BigInteger.toByteArray() may include leading zero byte for sign
            // Remove it if present to get original data
            if (bytes.isNotEmpty() && bytes[0] == 0.toByte())
            {
                bytes.copyOfRange(1, bytes.size)
            }
            else
            {
                bytes
            }
        }
    }

    // ========== Private Helper Methods ==========

    /**
     * Parses encoded symbols into a structured WSPR message.
     *
     * Symbol mapping:
     * - encodedSymbols[0]: Required 'Q' (ignored)
     * - encodedSymbols[1-6]: Callsign (6 characters)
     * - encodedSymbols[7-10]: Grid square (4 characters)
     * - encodedSymbols[11]: Power level (2-character representation)
     *
     * @param encodedSymbols List of ByteArrays from encoder
     * @return Structured WSPR message
     */
    private fun parseEncodedSymbolsToMessage(encodedSymbols: List<ByteArray>): WSPRDataMessage
    {
        require(encodedSymbols.size == WSPR_SYMBOLS.size)
        {
            "Invalid encoded symbol count: ${encodedSymbols.size}, expected: ${WSPR_SYMBOLS.size}"
        }

        // Extract callsign (symbols 1-6)
        val callsign = buildString {
            for (i in 1..6)
            {
                append(encodedSymbols[i].decodeToString())
            }
        }

        // Extract grid square (symbols 7 - 10)
        val gridSquare = buildString {
            for (i in 7..10)
            {
                append(encodedSymbols[i].decodeToString())
            }
        }

        // Extract power level (Symbol 11)
        val powerDbm = encodedSymbols[11].decodeToString().toInt()

        return WSPRDataMessage(
            callsign,
            gridSquare,
            powerDbm
        )
    }

    /**
     * Converts a WSPR message back to encoded symbol format for decoding.
     *
     * This reverses the parseEncodedSymbolsToMessage operation, reconstructing
     * the ByteArray list that the encoder originally produced.
     *
     * @param message WSPR message to convert
     * @return List of ByteArrays suitable for decoder
     */
    private fun convertMessageToEncodedSymbols(message: WSPRDataMessage): List<ByteArray>
    {
        val symbols = mutableListOf<ByteArray>()

        // Add the required prefix
        symbols.add("Q".toByteArray())

        // Add callsign characters (exactly 6 characters)
        val paddedCallsign = message.callsign.padEnd(6, ' ')
        require(paddedCallsign.length == 6)
        {
            "Callsign must be 6 characters or less: ${message.callsign}"
        }

        paddedCallsign.forEach { char ->
            symbols.add(char.toString().toByteArray())
        }

        // Add grid square characters (exactly 4 characters)
        require(message.gridSquare.length == 4)
        {
            "Grid square must be exactly 4 characters: ${message.gridSquare}"
        }

        message.gridSquare.forEach { char ->
            symbols.add(char.toString().toByteArray())
        }

        // Add power level
        symbols.add(message.powerDbm.toString().toByteArray())

        return symbols
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

/**
 * Exception thrown by WSPRCodex for encoding/decoding errors.
 *
 * @property message Descriptive error message
 * @property cause Optional underlying cause of the error
 */
class WSPRCodexException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)