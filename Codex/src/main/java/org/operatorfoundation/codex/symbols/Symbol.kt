package org.operatorfoundation.codex.symbols

import java.math.BigInteger

/**
 * Base interface for all symbol types that can encode/decode values.
 * Symbols represent different character sets or value ranges (e.g., letters, numbers, bytes).
 * Each symbol knows its size (number of possible values) and how to convert between
 * numeric representations and byte arrays.
 */
interface Symbol
{
    /**
     * Returns the number of possible values this symbol can represent.
     * For example, Binary returns 2, Byte returns 256.
     */
    fun size(): Int

    /**
     * Decodes a ByteArray to its numeric representation.
     * For example, CallLetterNumber decodes "A".toByteArray() to 0, "B".toByteArray() to 1, etc.
     *
     * @param encodedValue ByteArray containing the encoded data
     * @return The decoded numeric value as BigInteger
     */
    fun decode(encodedValue: ByteArray): BigInteger

    /**
     * Encodes a numeric value to its byte array representation.
     * For example, CallLetterNumber encodes 0 to "A".toByteArray(), 1 to "B".toByteArray(), etc.
     *
     * @param numericValue The numeric value to encode as BigInteger
     * @return ByteArray containing the encoded data
     */
    fun encode(numericValue: BigInteger): ByteArray
}