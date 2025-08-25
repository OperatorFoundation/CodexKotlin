package org.operatorfoundation.codex.symbols
import java.math.BigInteger

class CallLetterSpace : Symbol {
    companion object {
        private val charToValue = mapOf(
            "A" to 0.toBigInteger(), "B" to 1.toBigInteger(), "C" to 2.toBigInteger(),
            "D" to 3.toBigInteger(), "E" to 4.toBigInteger(), "F" to 5.toBigInteger(),
            "G" to 6.toBigInteger(), "H" to 7.toBigInteger(), "I" to 8.toBigInteger(),
            "J" to 9.toBigInteger(), "K" to 10.toBigInteger(), "L" to 11.toBigInteger(),
            "M" to 12.toBigInteger(), "N" to 13.toBigInteger(), "O" to 14.toBigInteger(),
            "P" to 15.toBigInteger(), "Q" to 16.toBigInteger(), "R" to 17.toBigInteger(),
            "S" to 18.toBigInteger(), "T" to 19.toBigInteger(), "U" to 20.toBigInteger(),
            "V" to 21.toBigInteger(), "W" to 22.toBigInteger(), "X" to 23.toBigInteger(),
            "Y" to 24.toBigInteger(), "Z" to 25.toBigInteger(), " " to 26.toBigInteger()
        )

        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
    }

    override fun size(): Int = 27

    override fun toString(): String = "CallLetterSpace"

    override fun decode(encodedValue: ByteArray): BigInteger {
        return charToValue[encodedValue.toString()]
            ?: throw IllegalArgumentException("CallLetterSpace, bad value: $encodedValue")
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Unknown value $numericValue for CallLetterSpace"))
            .toByteArray()
    }
}