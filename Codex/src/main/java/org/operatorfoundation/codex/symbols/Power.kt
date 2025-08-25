package org.operatorfoundation.codex.symbols
import java.math.BigInteger

class Power : Symbol {
    companion object {
        private val charToValue = mapOf(
            "0" to 0.toBigInteger(), "3" to 1.toBigInteger(), "7" to 2.toBigInteger(),
            "10" to 3.toBigInteger(), "13" to 4.toBigInteger(), "17" to 5.toBigInteger(),
            "20" to 6.toBigInteger(), "23" to 7.toBigInteger(), "27" to 8.toBigInteger(),
            "30" to 9.toBigInteger(), "33" to 10.toBigInteger(), "37" to 11.toBigInteger(),
            "40" to 12.toBigInteger(), "43" to 13.toBigInteger(), "47" to 14.toBigInteger(),
            "50" to 15.toBigInteger(), "53" to 16.toBigInteger(), "57" to 17.toBigInteger(),
            "60" to 18.toBigInteger()
        )

        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
    }

    override fun size(): Int = 19

    override fun toString(): String = "Power"

    override fun decode(encodedValue: ByteArray): BigInteger {
        return charToValue[encodedValue.decodeToString()]
            ?: throw IllegalArgumentException("Power, bad value $encodedValue")
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Invalid value $numericValue for Power"))
            .toByteArray()
    }
}