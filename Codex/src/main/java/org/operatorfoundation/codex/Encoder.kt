package org.operatorfoundation.codex

import org.operatorfoundation.codex.symbols.Symbol
import kotlin.math.min

/**
 * Encoder class that converts an integer to a list of encoded values.
 *
 * The encoder divides the integer into parts using a mixed-radix system,
 * where each symbol encodes a portion based on its size and position.
 *
 * @param symbols List of Symbol objects used for encoding
 */
class Encoder(private val symbols: List<Symbol>)
{
    /**
     * Creates a Decoder instance with the same symbol list.
     */
    fun decoder(): Decoder {
        return Decoder(symbols)
    }

    /**
     * Encodes an integer value into a list of ByteArrays.
     * Each ByteArray in the result corresponds to one symbol.
     *
     * @param integerToEncode The integer value to encode
     * @return List of ByteArrays, one for each symbol
     * @throws Exception if the value is too large to encode
     */
    fun encode(integerToEncode: Int): List<ByteArray>
    {
        val results = mutableListOf<ByteArray>()
        var remainingValue = integerToEncode

        // Process each symbol in sequence
        symbols.forEachIndexed { index, symbol ->
            val (encodedBytes, leftoverValue) = encodeStep(remainingValue, symbol, index)
            results.add(encodedBytes)
            remainingValue = leftoverValue
        }

        if (remainingValue != 0) {
            throw Exception("Encoder error, results: ${results.map { it.decodeToString() }}, leftover: $remainingValue")
        }

        return results
    }

    /**
     * Performs a single encode step for one symbol position.
     *
     * @param currentValue The value left to encode
     * @param symbol The symbol to use for encoding
     * @param index The position of this symbol in the list
     * @return Pair of (encoded ByteArray for this symbol, remaining value)
     */
    private fun encodeStep(currentValue: Int, symbol: Symbol, index: Int): Pair<ByteArray, Int>
    {
        if (symbol.size() == 1)
        {
            // Fixed symbols (size = 1) don't consume any of the value
            println("encode_step($currentValue, $symbol, $index)")

            // For debugging: show symbol capacity up to this point
            val symbolsUpToHere = if (index == 0)
            {
                symbols
            }
            else
            {
                symbols.subList(0, symbols.size - index)
            }
            
            val symbolSizes = symbolsUpToHere.map { it.size() }
            val totalCapacity = symbolSizes.fold(1) { acc, size -> acc * size }

            println("history: $symbolSizes, p: $totalCapacity")

            // Symbol with size 1 encodes its fixed value and passes through the current value
            val encodedBytes = symbol.encode(currentValue)
            val result = Pair(encodedBytes, currentValue)
            println("result: (${encodedBytes.decodeToString()}, $currentValue)")

            return result
        }
        else
        {
            println("encode_step($currentValue, $symbol, $index)")

            if (index == symbols.size - 1)
            {
                // Last symbol: encode all remaining value
                val encodedBytes = symbol.encode(currentValue)
                val result = Pair(encodedBytes, 0)
                println("result: (${encodedBytes.decodeToString()}, 0)")

                return result
            }
            else
            {
                // Calculate capacity of remaining symbols
                val remainingSymbols = symbols.subList(index + 1, symbols.size)
                val remainingSymbolSizes = remainingSymbols.map { it.size() }
                val remainingCapacity = remainingSymbolSizes.fold(1) { acc, size -> acc * size }

                println("history: $remainingSymbolSizes, p: $remainingCapacity")

                // Determine value for this symbol position
                // Division gives us how many "chunks" of remaining capacity we have
                val symbolValue = min(currentValue / remainingCapacity, symbol.size() - 1)
                println("n: $symbolValue")

                // Modulo gives us what's left for the remaining symbols
                val leftoverValue = currentValue % remainingCapacity
                println("m: $leftoverValue")

                // Encode this symbol's portion
                val encodedBytes = symbol.encode(symbolValue)
                val result = Pair(encodedBytes, leftoverValue)
                println("result: (${encodedBytes.decodeToString()}, $leftoverValue)")

                return result
            }
        }
    }
}