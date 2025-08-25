package org.operatorfoundation.codex.symbols
import java.math.BigInteger

class Trinary : Symbol {
    companion object {
        private val charToValue = mapOf(
            "0" to 0.toBigInteger(),
            "1" to 1.toBigInteger(),
            "2" to 2.toBigInteger()
        )

        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
    }

    override fun size(): Int = 3

    override fun toString(): String = "Trinary"

    override fun decode(encodedValue: ByteArray): BigInteger {
        return charToValue[encodedValue.toString()]
            ?: throw IllegalArgumentException("Trinary, bad value $encodedValue")
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Unknown value $numericValue for Trinary"))
            .toByteArray()
    }
}