package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class Trinary(val value: Int) : Symbol {
    companion object : SymbolFactory<Trinary> {
        const val MAX = 2

        override fun size(): Int = MAX + 1

        /**
         * Encodes a numeric value to its byte array representation.
         * For example, CallLetterNumber encodes 0 to "A".toByteArray(), 1 to "B".toByteArray(), etc.
         *
         * @param numericValue The numeric value to encode as BigInteger
         * @return ByteArray containing the encoded data
         */
        override fun encode(numericValue: BigInteger): Trinary {
            val intValue = numericValue.toInt()
            if (intValue < 0 || intValue > MAX) {
                throw IllegalArgumentException("Value $numericValue must be between 0 and $MAX")
            }
            return Trinary(intValue)
        }
    }

    init {
        require(value in 0..MAX) { "Trinary value must be 0 or 1, got $value" }
    }

    override fun toString(): String = "Trinary($value)"

    override fun decode(): BigInteger {
        return value.toBigInteger()
    }
}