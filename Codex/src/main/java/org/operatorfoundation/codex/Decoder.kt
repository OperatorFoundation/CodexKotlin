package org.operatorfoundation.codex

import org.operatorfoundation.codex.symbols.Symbol
import java.math.BigInteger

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
    fun decode(encodedValues: List<ByteArray>): BigInteger
    {
        val results = mutableListOf<BigInteger>()

        // Process each symbol with its corresponding encoded value
        symbols.forEachIndexed { index, symbol ->
            val encodedValue = encodedValues[index]
            val result = decodeStep(encodedValue, symbol, index)
            results.add(result)
        }

        return results.reduce(BigInteger::add)
    }

    /**
     * Performs a single decode step for one symbol position.
     *
     * @param encodedValue The ByteArray to decode for this symbol
     * @param symbol The symbol to use for decoding
     * @param index The position of this symbol in the list
     * @return The numeric contribution of this symbol to the total
     */
    private fun decodeStep(encodedValue: ByteArray, symbol: Symbol, index: Int): BigInteger
    {
        if (symbol.size() == 1)
        {
            // Symbols with size 1 don't contribute to the numeric value
            println("decode_step(encoded value: ${encodedValue.decodeToString()}, symbol: $symbol, index: $index)")

            return 0.toBigInteger()
        }
        else
        {
            println("decode_step(encoded value: ${encodedValue.decodeToString()}, symbol: $symbol, index: $index)")

            if (index == symbols.size - 1)
            {
                // Last symbol: just return its decoded value
                return symbol.decode(encodedValue)
            }
            else
            {
                // Calculate product of remaining symbol sizes
                val remainingSymbols = symbols.subList(index + 1, symbols.size)
                val remainingSymbolSizes = remainingSymbols.map { it.size() }
                val positionMultiplier = remainingSymbolSizes.fold(1) { acc, size -> acc * size }

                println("history:/n  remaining symbol sizes - $remainingSymbolSizes, position multiplier: $positionMultiplier")

                // Multiply decoded value by position weight
                val result = symbol.decode(encodedValue) * positionMultiplier.toBigInteger()
                println("result: $result")

                return result
            }
        }
    }
}
