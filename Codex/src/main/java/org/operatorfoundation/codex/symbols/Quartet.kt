package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class Quartet(val value: Int) : Symbol {
    companion object : SymbolFactory<Quartet> {
        const val MAX = 3

        override fun size(): BigInteger = (MAX + 1).toBigInteger()

        override fun encode(numericValue: BigInteger): Quartet {
            val intValue = numericValue.toInt()
            if (intValue < 0 || intValue > MAX) {
                throw IllegalArgumentException("Value $numericValue must be between 0 and $MAX")
            }
            return Quartet(intValue)
        }
    }

    init {
        require(value in 0..MAX) { "Quartet value must be 0 and $MAX, got $value" }
    }

    override fun toString(): String = "Quartet($value)"

    override fun decode(): BigInteger {
        return value.toBigInteger()
    }

    fun toByte(): Byte
    {
        return value.toByte()
    }
}