package org.operatorfoundation.codex.symbols
import java.math.BigInteger

class GridLetter : Symbol {
    companion object {
        private val charToValue = mapOf(
            "A" to 0.toBigInteger(), "B" to 1.toBigInteger(), "C" to 2.toBigInteger(),
            "D" to 3.toBigInteger(), "E" to 4.toBigInteger(), "F" to 5.toBigInteger(),
            "G" to 6.toBigInteger(), "H" to 7.toBigInteger(), "I" to 8.toBigInteger(),
            "J" to 9.toBigInteger(), "K" to 10.toBigInteger(), "L" to 11.toBigInteger(),
            "M" to 12.toBigInteger(), "N" to 13.toBigInteger(), "O" to 14.toBigInteger(),
            "P" to 15.toBigInteger(), "Q" to 16.toBigInteger(), "R" to 17.toBigInteger()
        )

        private val valueToChar = (0..17).associate {
            it.toBigInteger() to (65 + it).toChar().toString()
        }
    }

    override fun size(): Int = 18

    override fun toString(): String = "GridLetter"

    override fun decode(encodedValue: ByteArray): BigInteger {
        return charToValue[encodedValue.decodeToString()]
            ?: throw IllegalArgumentException("GridLetter, bad value $encodedValue")
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Unknown value $numericValue for GridLetter"))
            .toByteArray()
    }
}