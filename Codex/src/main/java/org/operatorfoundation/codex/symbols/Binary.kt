package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class Binary(val value: Int) : Symbol {
    companion object : SymbolFactory<Binary> {
        const val MAX = 1

        override fun size(): BigInteger = (MAX + 1).toBigInteger()

        /**
         * Encodes a numeric value to its byte array representation.
         * For example, CallLetterNumber encodes 0 to "A".toByteArray(), 1 to "B".toByteArray(), etc.
         *
         * @param numericValue The numeric value to encode as BigInteger
         * @return ByteArray containing the encoded data
         */
        override fun encode(numericValue: BigInteger): Binary {
            val intValue = numericValue.toInt()
            if (intValue < 0 || intValue > MAX) {
                throw IllegalArgumentException("Value $numericValue must be between 0 and $MAX")
            }
            return Binary(intValue)
        }
    }

    init {
        require(value in 0..MAX) { "Binary value must be 0 or 1, got $value" }
    }

    override fun toString(): String = "Binary($value)"

    override fun decode(): BigInteger {
        return value.toBigInteger()
    }
}