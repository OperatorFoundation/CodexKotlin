package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class GridLetter(val value: Char) : Symbol {
    companion object : SymbolFactory<GridLetter> {
        private val charToValue = mapOf(
            'A' to 0, 'B' to 1, 'C' to 2,
            'D' to 3, 'E' to 4, 'F' to 5,
            'G' to 6, 'H' to 7, 'I' to 8,
            'J' to 9, 'K' to 10, 'L' to 11,
            'M' to 12, 'N' to 13, 'O' to 14,
            'P' to 15, 'Q' to 16, 'R' to 17
        )
        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
        
        override fun size(): BigInteger = 18.toBigInteger()

        override fun encode(numericValue: BigInteger): GridLetter {
            val int = numericValue.toInt()
            val char = valueToChar[int]
            if (char != null) {
                return GridLetter(char)
            }
            throw IllegalArgumentException("Unknown value $numericValue for GridLetter")
        }

        /**
         * Creates a GridLetter from a character value.
         *
         * @param char The character (A-R)
         * @return GridLetter instance
         * @throws IllegalArgumentException if char is not a valid GridLetter
         */
        fun fromChar(char: Char): GridLetter
        {
            val upperChar = char.uppercaseChar()
            if (charToValue.containsKey(upperChar)) return GridLetter(upperChar)
            throw IllegalArgumentException("Invalid GridLetter character: $char")
        }
    }
    
    override fun toString(): String = "GridLetter($value)"

    override fun decode(): BigInteger {
        val int = charToValue[value]
        if (int != null) {
            return int.toBigInteger()
        }
        throw IllegalArgumentException("GridLetter, bad value: $value")
    }
}