package org.operatorfoundation.codex.symbols

import java.math.BigInteger
import org.operatorfoundation.codex.Encoder

/**
 * A single WSPR message symbol that encodes/decodes one complete WSPR transmission.
 *
 * Format: Q + 6-char callsign + 4-char grid + power level = 12 bytes total
 */
class WSPRMessage : Symbol {
    /**
     * WSPR symbol configuration matching the standard WSPR message format.
     *
     * Symbol breakdown:
     * - Required('Q'): Fixed prefix (1 byte, size=1, contributes 0 bits)
     * - CallLetterNumber (6x): Callsign characters (A-Z, 0-9 = 36 values each)
     * - GridLetter (2x): First two grid characters (A-R = 18 values each)
     * - Number (2x): Last two grid characters (0-9 = 10 values each)
     * - Power: Power level (19 discrete values: 0, 3, 7, 10... 60 dBm)
     */
    private val WSPR_SYMBOLS: List<Symbol> = listOf(
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

    private val encoder = Encoder(WSPR_SYMBOLS)
    private val decoder = encoder.decoder()

    override fun size(): Int = WSPR_SYMBOLS.sumOf { it.size() }

    override fun toString(): String = "WSPRMessage"

    override fun decode(encodedValue: ByteArray): BigInteger {
        require(encodedValue.size == size()) {
            "Encoded value must be ${size()} bytes, got ${encodedValue.size}"
        }

        // Split the encoded byte array into parts for each symbol
        val parts = mutableListOf<ByteArray>()
        var offset = 0

        for (symbol in WSPR_SYMBOLS) {
            val symbolSize = symbol.size()
            val symbolBytes = encodedValue.sliceArray(offset until offset + symbolSize)
            parts.add(symbolBytes)
            offset += symbolSize
        }

        // Use the decoder to convert the parts back to an integer
        return decoder.decode(parts)
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        // Use the encoder to convert the integer to a list of byte arrays
        val parts = encoder.encode(numericValue)

        // Concatenate all parts into a single byte array
        return parts.flatMap { it.toList() }.toByteArray()
    }
}