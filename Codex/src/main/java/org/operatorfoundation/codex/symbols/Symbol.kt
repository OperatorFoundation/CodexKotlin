package org.operatorfoundation.codex.symbols

import java.math.BigInteger

/**
 * Base interface for all symbol types that can encode/decode values.
 * Symbols represent different character sets or value ranges (e.g., letters, numbers, bytes).
 * Each symbol knows its size (number of possible values) and how to convert between
 * integer representations and actual values (characters, strings, or integers).
 */
interface Symbol
{
    /**
     * Returns the number of possible values this symbol can represent.
     * For example, Binary returns 2, Byte returns 256.
     */
    fun size(): Int

    /**
     * Decodes an input value (Char, String, or Int) to its numeric representation.
     * For example, CallLetterNumber decodes 'A' to 0, 'B' to 1, etc.
     *
     * @param encodedValue Can be Char, String, or Int depending on the symbol
     * @return The decoded integer value
     */
    fun decode(encodedValue: ByteArray): BigInteger

    /**
     * Encodes a numeric value to its symbol representation.
     * For example, CallLetterNumber encodes 0 to 'A', 1 to 'B', etc.
     *
     * @param numericValue The integer to encode
     * @return Can be Char, String, or Int depending on the symbol
     */
    fun encode(numericValue: BigInteger): ByteArray
}