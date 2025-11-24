package org.operatorfoundation.codex

import java.math.BigInteger

interface SymbolFactory<T: Symbol>
{
    fun encode(numericValue: BigInteger): T

    /**
     * Returns the number of possible values this symbol can represent.
     * For example, Binary returns 2, Byte returns 256.
     */
    fun size(): BigInteger
}

/**
 * Base interface for all symbol types that can encode/decode values.
 * Symbols represent different character sets or value ranges (e.g., letters, numbers, bytes).
 * Each symbol knows its size (number of possible values) and how to convert between
 * numeric representations and byte arrays.
 */
interface Symbol
{
    /**
     * Decodes a ByteArray to its numeric representation.
     * For example, CallLetterNumber decodes "A".toByteArray() to 0, "B".toByteArray() to 1, etc.
     *
     * @param encodedValue ByteArray containing the encoded data
     * @return The decoded numeric value as BigInteger
     */
    fun decode(): BigInteger
}