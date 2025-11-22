package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class CallLetterSpace(val value: Char) : Symbol {

    companion object : SymbolFactory<CallLetterSpace> {
        private val charToValue = mapOf(
            'A' to 0, 'B' to 1, 'C' to 2,
            'D' to 3, 'E' to 4, 'F' to 5,
            'G' to 6, 'H' to 7, 'I' to 8,
            'J' to 9, 'K' to 10, 'L' to 11,
            'M' to 12, 'N' to 13, 'O' to 14,
            'P' to 15, 'Q' to 16, 'R' to 17,
            'S' to 18, 'T' to 19, 'U' to 20,
            'V' to 21, 'W' to 22, 'X' to 23,
            'Y' to 24, 'Z' to 25, ' ' to 26
        )
        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()

        override fun size(): Int = 27

        override fun encode(numericValue: BigInteger): CallLetterSpace {
            val int = numericValue.toInt()
            val char = valueToChar[int]
            if (char != null) {
                return CallLetterSpace(char)
            }
            throw IllegalArgumentException("Unknown value $numericValue for CallLetterSpace")
        }
    }

    override fun toString(): String = "CallLetterSpace($value)"

    override fun decode(): BigInteger {
        val int = charToValue[value]
        if (int != null) {
            return int.toBigInteger()
        }
        throw IllegalArgumentException("CallLetterSpace, bad value: $value")
    }
}