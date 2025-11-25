package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class CallLetter(val value: Char) : Symbol {
    companion object : SymbolFactory<CallLetter> {
        private val charToValue = mapOf(
            'A' to 0, 'B' to 1, 'C' to 2,
            'D' to 3, 'E' to 4, 'F' to 5,
            'G' to 6, 'H' to 7, 'I' to 8,
            'J' to 9, 'K' to 10, 'L' to 11,
            'M' to 12, 'N' to 13, 'O' to 14,
            'P' to 15, 'Q' to 16, 'R' to 17,
            'S' to 18, 'T' to 19, 'U' to 20,
            'V' to 21, 'W' to 22, 'X' to 23,
            'Y' to 24, 'Z' to 25
        )
        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()

        override fun size(): BigInteger = 26.toBigInteger()

        override fun encode(numericValue: BigInteger): CallLetter {
            val int = numericValue.toInt()
            val char = valueToChar[int]
                ?: throw IllegalArgumentException("Unknown value $numericValue for CallLetter")
            return CallLetter(char)
        }

        /**
         * Creates a CallLetter from a character value.
         *
         * @param char The character (A-Z)
         * @return CallLetter instance
         * @throws IllegalArgumentException if char is not a valid CallLetter
         */
        fun fromChar(char: Char): CallLetter
        {
            val upperChar = char.uppercaseChar()
            if (charToValue.containsKey(upperChar)) return CallLetter(upperChar)
            throw IllegalArgumentException("Invalid CallLetter character: $char")
        }
    }

    override fun toString(): String = "CallLetter($value)"

    override fun decode(): BigInteger {
        val int = charToValue[value]
            ?: throw IllegalArgumentException("CallLetter, bad value: ${value}")
        return int.toBigInteger()
    }
}