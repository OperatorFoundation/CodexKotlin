package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class CallLetterNumber(val value: Char) : Symbol {
    companion object : SymbolFactory<CallLetterNumber> {
        private val charToValue = mapOf(
            'A' to 0, 'B' to 1, 'C' to 2,
            'D' to 3, 'E' to 4, 'F' to 5,
            'G' to 6, 'H' to 7, 'I' to 8,
            'J' to 9, 'K' to 10, 'L' to 11,
            'M' to 12, 'N' to 13, 'O' to 14,
            'P' to 15, 'Q' to 16, 'R' to 17,
            'S' to 18, 'T' to 19, 'U' to 20,
            'V' to 21, 'W' to 22, 'X' to 23,
            'Y' to 24, 'Z' to 25, '0' to 26,
            '1' to 27, '2' to 28, '3' to 29,
            '4' to 30, '5' to 31, '6' to 32,
            '7' to 33, '8' to 34, '9' to 35
        )
        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()

        override fun size(): Int = 36

        override fun encode(numericValue: BigInteger): CallLetterNumber {
            val int = numericValue.toInt()
            val char = valueToChar[int]
            if (char != null) {
                return CallLetterNumber(char)
            }
            throw IllegalArgumentException("Unknown value $numericValue for CallLetterNumber")
        }
    }

    override fun toString(): String = "CallLetterNumber($value)"

    override fun decode(): BigInteger {
        val int = charToValue[value]
        if (int != null) {
            return int.toBigInteger()
        }
        throw IllegalArgumentException("CallLetterNumber, bad value: $value")
    }
}