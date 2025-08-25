<<<<<<< HEAD:Codex/src/main/java/org/operatorfoundation/codex/symbols/Byte.kt
package org.operatorfoundation.codex.symbols


import kotlin.math.min

/**
 * Interface for symbol objects that can encode/decode values.
 *
 * Symbols represent different character sets or value ranges that can be encoded/decoded.
 * For example, a Binary symbol can encode/decode 0 or 1, while a CallLetterNumber
 * can handle A-Z and 0-9.
 */
interface Symbol {
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
    fun decode(encodedValue: Any): Int

    /**
     * Encodes a numeric value to its symbol representation.
     * For example, CallLetterNumber encodes 0 to 'A', 1 to 'B', etc.
     *
     * @param numericValue The integer to encode
     * @return Can be Char, String, or Int depending on the symbol
     */
    fun encode(numericValue: Int): Any
}

/**
 * Decoder class that converts encoded symbols back to their original numeric value.
 *
 * The decoder uses a list of symbols to interpret encoded data. Each symbol in the
 * list represents a different position in the encoding, with different value ranges.
 * The decoder calculates the total numeric value by considering each symbol's
 * contribution based on its position and the sizes of subsequent symbols.
 *
 * @param symbols List of Symbol objects used for decoding
 */
class Decoder(private val symbols: List<Symbol>) {

    /**
     * Creates an Encoder instance with the same symbol list.
     * Useful for round-trip encoding/decoding operations.
     */
    fun encoder(): Encoder {
        return Encoder(symbols)
    }

    /**
     * Decodes a list of encoded values back to the original integer.
     *
     * The decoding process works by treating the symbols as a mixed-radix number system.
     * Each symbol position contributes to the final value based on the product of all
     * subsequent symbol sizes.
     *
     * @param encodedValues Can be String (for character data), ByteArray, or List
     * @return The decoded integer value
     */
    fun decode(encodedValues: Any): Int {
        val decodedResults = mutableListOf<Int>()

        // Convert input to a list we can iterate over
        val encodedList = when (encodedValues) {
            is String -> encodedValues.toList()  // Convert string to list of chars
            is ByteArray -> encodedValues.map { it.toInt() }  // Convert bytes to ints
            is List<*> -> encodedValues
            else -> throw IllegalArgumentException("Unsupported input type: ${encodedValues::class}")
        }

        // Process each symbol with its corresponding encoded value
        symbols.forEachIndexed { symbolIndex, symbol ->
            val encodedValue = encodedList[symbolIndex]!!
            val decodedValue = decodeStep(encodedValue, symbol, symbolIndex)
            decodedResults.add(decodedValue)
        }

        // Sum all decoded values to get the final result
        return decodedResults.sum()
    }

    /**
     * Performs a single decode step for one symbol position.
     *
     * The contribution of each symbol to the final value depends on:
     * - Its decoded value (from the symbol's decode method)
     * - Its position in the symbol list
     * - The sizes of all symbols that come after it
     *
     * @param encodedValue The encoded value to decode
     * @param symbol The symbol to use for decoding
     * @param symbolIndex The position of this symbol in the list
     * @return The numeric contribution of this symbol to the total
     */
    private fun decodeStep(encodedValue: Any, symbol: Symbol, symbolIndex: Int): Int {
        // Symbols with size 1 (like Required) don't contribute to the numeric value
        // They're used for validation only
        return if (symbol.size() == 1) {
            println("decode_step($encodedValue, $symbol, $symbolIndex)")
            0
        } else {
            println("decode_step($encodedValue, $symbol, $symbolIndex)")

            if (symbolIndex == symbols.size - 1) {
                // Last symbol: its contribution is just its decoded value
                symbol.decode(encodedValue)
            } else {
                // Calculate the multiplier based on remaining symbols
                // This implements a mixed-radix number system
                val remainingSymbols = symbols.subList(symbolIndex + 1, symbols.size)
                val remainingSizes = remainingSymbols.map { it.size() }

                // Product of all remaining symbol sizes determines this position's weight
                val positionMultiplier = remainingSizes.fold(1) { acc, size -> acc * size }

                println("history: $remainingSizes, p: $positionMultiplier")

                // This symbol's contribution = decoded value * position weight
                val contribution = symbol.decode(encodedValue) * positionMultiplier
                println("result: $contribution")

                contribution
            }
        }
    }
}

/**
 * Encoder class that converts an integer to a list of encoded symbols.
 *
 * The encoder uses a list of symbols to represent different parts of the integer.
 * It works like converting to a mixed-radix number system, where each position
 * can have a different base (determined by the symbol's size).
 *
 * @param symbols List of Symbol objects used for encoding
 */
class Encoder(private val symbols: List<Symbol>) {

    /**
     * Creates a Decoder instance with the same symbol list.
     * Useful for round-trip encoding/decoding operations.
     */
    fun decoder(): Decoder {
        return Decoder(symbols)
    }

    /**
     * Encodes an integer value into a list of symbol representations.
     *
     * The encoding process divides the input number into parts, with each symbol
     * encoding a portion based on its size and position. This is similar to
     * converting a decimal number to a mixed-radix representation.
     *
     * @param valueToEncode The integer value to encode
     * @return List of encoded values, one for each symbol
     * @throws Exception if the value is too large to encode with the given symbols
     */
    fun encode(valueToEncode: Int): List<Any> {
        val encodedResults = mutableListOf<Any>()
        var remainingValue = valueToEncode

        // Process each symbol in sequence
        symbols.forEachIndexed { symbolIndex, symbol ->
            val (encodedPart, leftoverValue) = encodeStep(remainingValue, symbol, symbolIndex)
            encodedResults.add(encodedPart)
            remainingValue = leftoverValue
        }

        // If there's a leftover value, the input was too large for our symbol set
        if (remainingValue != 0) {
            throw Exception("Encoder error, results: $encodedResults, leftover: $remainingValue")
        }

        return encodedResults
    }

    /**
     * Performs a single encode step for one symbol position.
     *
     * For symbols with size > 1:
     * - Determines how much of the remaining value this symbol should encode
     * - Uses division and modulo based on the product of remaining symbol sizes
     *
     * For symbols with size = 1:
     * - These are fixed values (like Required symbols) that don't encode data
     *
     * @param remainingValue The value left to encode
     * @param symbol The symbol to use for encoding
     * @param symbolIndex The position of this symbol in the list
     * @return Pair of (encoded result for this symbol, remaining value for next symbols)
     */
    private fun encodeStep(remainingValue: Int, symbol: Symbol, symbolIndex: Int): Pair<Any, Int> {
        return if (symbol.size() == 1) {
            // Fixed symbols (size = 1) don't consume any of the value
            println("encode_step($remainingValue, $symbol, $symbolIndex)")

            // Calculate product of all symbols up to this point (for debugging)
            val relevantSymbols = if (symbolIndex == 0) {
                symbols
            } else {
                symbols.subList(0, symbols.size - symbolIndex)
            }
            val symbolSizes = relevantSymbols.map { it.size() }
            val product = symbolSizes.fold(1) { acc, size -> acc * size }

            println("history: $symbolSizes, p: $product")

            val result = Pair(symbol.encode(remainingValue), remainingValue)
            println("result: $result")
            result
        } else {
            println("encode_step($remainingValue, $symbol, $symbolIndex)")

            if (symbolIndex == symbols.size - 1) {
                // Last symbol: encode all remaining value
                val result = Pair(symbol.encode(remainingValue), 0)
                println("result: $result")
                result
            } else {
                // Calculate how much this symbol should encode based on remaining symbols
                val remainingSymbols = symbols.subList(symbolIndex + 1, symbols.size)
                val remainingSizes = remainingSymbols.map { it.size() }
                val remainingCapacity = remainingSizes.fold(1) { acc, size -> acc * size }

                println("history: $remainingSizes, p: $remainingCapacity")

                // Determine the value for this symbol position
                // Use min to ensure we don't exceed the symbol's capacity
                val symbolValue = min(remainingValue / remainingCapacity, symbol.size() - 1)
                println("n: $symbolValue")

                // Calculate what's left for the remaining symbols
                val leftoverValue = remainingValue % remainingCapacity
                println("m: $leftoverValue")

                val result = Pair(symbol.encode(symbolValue), leftoverValue)
                println("result: $result")
                result
            }
        }
    }
}

// ===== Symbol Implementations =====

/**
 * Required symbol - validates that a specific character appears at this position.
 * Used for fixed protocol headers or delimiters.
 */
class Required(private val requiredChar: Char) : Symbol {
    override fun size() = 1  // Size 1 means it doesn't contribute to the numeric value
    override fun toString() = "Required($requiredChar)"

    override fun encode(numericValue: Int): Char = requiredChar

    override fun decode(encodedValue: Any): Int {
        if (encodedValue != requiredChar) {
            throw IllegalArgumentException("Required($requiredChar) != $encodedValue")
        }
        return 0  // No numeric contribution
    }
}
=======
import org.operatorfoundation.Codex.Symbols.Symbol
>>>>>>> 2bc9abdeffd8d87b8e20f51ca11d5716a67dc80d:Codex/src/main/java/org/operatorfoundation/Codex/symbols/Byte.kt

/**
 * Byte symbol - encodes/decodes byte values (0-255).
 * Used for binary data encoding.
 */
class Byte : Symbol {
    override fun size() = 256
    override fun toString() = "Byte"

    override fun encode(numericValue: Int): Int {
        if (numericValue < 0 || numericValue > 255) {
            throw Exception("Invalid value $numericValue for Byte (must be 0-255)")
        }
        return numericValue
    }

    override fun decode(encodedValue: Any): Int {
        val value = when (encodedValue) {
            is Int -> encodedValue
            is kotlin.Byte -> encodedValue.toInt() and 0xFF  // Handle signed bytes properly
            is Char -> encodedValue.code  // ASCII value of character
            else -> throw Exception("Invalid type for Byte decode: ${encodedValue::class}")
        }
        if (value < 0 || value > 255) {
            throw Exception("Invalid value $value for Byte (must be 0-255)")
        }
        return value
    }
}
