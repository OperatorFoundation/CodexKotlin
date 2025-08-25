package org.operatorfoundation.codex

import org.operatorfoundation.Codex.Symbols.Symbol

class Decoder(private val symbols: List<Symbol>)
{
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
    fun decode(encodedValues: Any): Int
    {
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
    private fun decodeStep(encodedValue: Any, symbol: Symbol, symbolIndex: Int): Int
    {
        // Symbols with size 1 (like Required) don't contribute to the numeric value
        // They're used for validation only
        return if (symbol.size() == 1) {
            println("decode_step($encodedValue, $symbol, $symbolIndex)")
            0
        }
        else
        {
            println("decode_step($encodedValue, $symbol, $symbolIndex)")

            if (symbolIndex == symbols.size - 1)
            {
                // Last symbol: its contribution is just its decoded value
                symbol.decode(encodedValue)
            }
            else
            {
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

//class Decoder:
//    def __init__(self, bs):
//self.bs = bs
//
//def encoder(self):
//return Encoder(self.bs)
//
//def decode(self, ns):
//results = []
//for index, b in enumerate(self.bs):
//n = ns[index]
//result = self.decode_step(n, b, index)
//results.append(result)
//
//return sum(results)
//
//def decode_step(self, n, b, index):
//if len(b) == 1:
//print('decode_step({n}, {b}, {index})'.format(n=n, b=b, index=index))
//return 0
//else:
//print('decode_step({n}, {b}, {index})'.format(n=n, b=b, index=index))
//if index == len(self.bs) - 1:
//return b.decode(n)
//else:
//history = self.bs[index + 1:]
//lens = list(map(lambda b: len(b), history))
//p = math.prod(lens)
//print('history: {lens}, p: {p}'.format(lens=lens, p=p))
//result = b.decode(n) * p
//print('result: {result}'.format(result=result))
//return result