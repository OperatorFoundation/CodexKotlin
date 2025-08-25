package org.operatorfoundation.Codex.Symbols

/**
 * Base interface for all symbol types that can encode/decode values.
 * Symbols represent different character sets or value ranges (e.g., letters, numbers, bytes).
 * Each symbol knows its size (number of possible values) and how to convert between
 * integer representations and actual values (characters, strings, or integers).
 */
interface Symbol
{
    /**
     * Returns the number of possible values this symbol can represent
     */
    fun size(): Int

    /**
     * Decodes a value using this symbol
     * @param n Can be Char, String, or Int depending on the symbol
     */
    fun decode(n: Any): Int

    /**
     * Encodes a value using this symbol
     * @return Can be Char, String, or Int depending on the symbol
     */
    fun encode(i: Int): Any
}