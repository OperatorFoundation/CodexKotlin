package org.operatorfoundation.codex

import org.operatorfoundation.codex.symbols.Symbol

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
     * @param encodedValues Can be String (for character data), ByteArray, or List<String>
     * @return The decoded integer value
     */
    fun decode(encodedValues: Any): Int {
        val decodedResults = mutableListOf<Int>()

        // Convert input to a list of strings we can iterate over
        val encodedList: List<String> = when (encodedValues) {
            is String -> encodedValues.map { it.toString() }  // Convert each char to string
            is ByteArray -> encodedValues.map { it.toInt().toString() }  // Convert bytes to string ints
            is List<*> -> encodedValues.map { it.toString() }  // Convert each element to string
            else -> throw IllegalArgumentException("Unsupported input type: ${encodedValues::class}")
        }

        // Process each symbol with its corresponding encoded value
        symbols.forEachIndexed { symbolIndex, symbol ->
            val encodedValue = encodedList[symbolIndex]
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
     * @param encodedValue The encoded string value to decode
     * @param symbol The symbol to use for decoding
     * @param symbolIndex The position of this symbol in the list
     * @return The numeric contribution of this symbol to the total
     */
    private fun decodeStep(encodedValue: String, symbol: Symbol, symbolIndex: Int): Int {
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