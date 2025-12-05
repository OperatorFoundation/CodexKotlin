package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class GridNumber(val value: Char) : Symbol {
    companion object : SymbolFactory<GridNumber> {
        private val charToValue = mapOf(
            '0' to 0, '1' to 1, '2' to 2,
            '3' to 3, '4' to 4, '5' to 5,
            '6' to 6, '7' to 7, '8' to 8,
            '9' to 9
        )
        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
        
        override fun size(): BigInteger = 10.toBigInteger()

        override fun encode(numericValue: BigInteger): GridNumber {
            val int = numericValue.toInt()
            val char = valueToChar[int]
            if (char != null) {
                return GridNumber(char)
            }
            throw IllegalArgumentException("Unknown value $numericValue for Number")
        }

        /**
         * Creates a GridNumber from a character value.
         *
         * @param char The character (0-9)
         * @return GridNumber instance
         * @throws IllegalArgumentException if char is not a valid digit
         */
        fun fromChar(char: Char): GridNumber
        {
            if (charToValue.containsKey(char)) return GridNumber(char)
            throw IllegalArgumentException("Invalid GridNumber character: $char")
        }
    }

    override fun toString(): String = "Number($value)"

    override fun decode(): BigInteger {
        val int = charToValue[value]
        if (int != null) {
            return int.toBigInteger()
        }
        throw IllegalArgumentException("Number, bad value: $value")
    }
}