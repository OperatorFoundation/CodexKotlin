package org.operatorfoundation.Codex

import org.operatorfoundation.Codex.Symbols.Symbol
import kotlin.math.min

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