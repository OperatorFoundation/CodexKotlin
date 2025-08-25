package org.operatorfoundation.codex.symbols
import java.math.BigInteger

class Number : Symbol {
    companion object {
        private val charToValue = mapOf(
            "0" to 0.toBigInteger(), "1" to 1.toBigInteger(), "2" to 2.toBigInteger(),
            "3" to 3.toBigInteger(), "4" to 4.toBigInteger(), "5" to 5.toBigInteger(),
            "6" to 6.toBigInteger(), "7" to 7.toBigInteger(), "8" to 8.toBigInteger(),
            "9" to 9.toBigInteger()
        )

        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
    }

    override fun size(): Int = 10

    override fun toString(): String = "Number"

    override fun decode(encodedValue: ByteArray): BigInteger {
        return charToValue[encodedValue.decodeToString()]
            ?: throw IllegalArgumentException("Number, bad value: $encodedValue")
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Unknown value $numericValue for Number"))
            .toByteArray()
    }
}