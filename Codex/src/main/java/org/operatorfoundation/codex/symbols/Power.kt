package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class Power(val value: Int) : Symbol {
    companion object : SymbolFactory<Power> {
        private val indexToPower = mapOf(
            0 to 0, 1 to 3, 2 to 7, 3 to 10, 4 to 13, 5 to 17,
            6 to 20, 7 to 23, 8 to 27, 9 to 30, 10 to 33, 11 to 37,
            12 to 40, 13 to 43, 14 to 47, 15 to 50, 16 to 53, 17 to 57,
            18 to 60
        )
        private val powerToIndex = indexToPower.map { (k, v) -> v to k }.toMap()

        override fun size(): BigInteger = 19.toBigInteger()

        override fun encode(numericValue: BigInteger): Power {
            val powerValue = indexToPower[numericValue.toInt()]
                ?: throw IllegalArgumentException("Invalid index: $numericValue")
            return Power(powerValue)
        }
    }

    override fun toString(): String = "Power($value)"

    override fun decode(): BigInteger {
        val encodedIndex = powerToIndex[value]
            ?: throw IllegalArgumentException("Invalid power value: $value")
        return encodedIndex.toBigInteger()
    }
}