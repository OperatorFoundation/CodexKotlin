package org.operatorfoundation.codex

import org.operatorfoundation.codex.symbols.Power
import org.operatorfoundation.codex.symbols.Binary
import org.operatorfoundation.codex.symbols.Symbol

/**
 * Decoder class that converts a list of encoded values back to an integer.
 *
 * The decoder uses a list of symbols to interpret encoded data. Each position
 * in the input list corresponds to one symbol, and the decoder calculates
 * the total numeric value using a mixed-radix number system.
 *
 * @param symbols List of Symbol objects used for decoding
 */
class Decoder(private val symbols: List<Symbol>)
{

    /**
     * Creates an Encoder instance with the same symbol list.
     */
    fun encoder(): Encoder {
        return Encoder(symbols)
    }

    /**
     * Decodes a list of ByteArrays back to the original integer.
     * Each ByteArray in the list corresponds to one symbol at the same index.
     *
     * @param encodedValues List of ByteArrays, one for each symbol
     * @return The decoded integer value
     */
    fun decode(encodedValues: List<ByteArray>): Int {
        val results = mutableListOf<Int>()

        // Process each symbol with its corresponding encoded value
        symbols.forEachIndexed { index, symbol ->
            val encodedValue = encodedValues[index]
            val result = decodeStep(encodedValue, symbol, index)
            results.add(result)
        }

        return results.sum()
    }

    /**
     * Performs a single decode step for one symbol position.
     *
     * @param encodedValue The ByteArray to decode for this symbol
     * @param symbol The symbol to use for decoding
     * @param index The position of this symbol in the list
     * @return The numeric contribution of this symbol to the total
     */
    private fun decodeStep(encodedValue: ByteArray, symbol: Symbol, index: Int): Int
    {
        return if (symbol.size() == 1) {
            // Symbols with size 1 don't contribute to the numeric value
            println("decode_step(${encodedValue.decodeToString()}, $symbol, $index)")
            0
        }
        else
        {
            println("decode_step(${encodedValue.decodeToString()}, $symbol, $index)")

            if (index == symbols.size - 1)
            {
                // Last symbol: just return its decoded value
                symbol.decode(encodedValue)
            }
            else
            {
                // Calculate product of remaining symbol sizes
                val history = symbols.subList(index + 1, symbols.size)
                val lens = history.map { it.size() }
                val p = lens.fold(1) { acc, size -> acc * size }

                println("history: $lens, p: $p")

                // Multiply decoded value by position weight
                val result = symbol.decode(encodedValue) * p
                println("result: $result")

                result
            }
        }
    }
}
