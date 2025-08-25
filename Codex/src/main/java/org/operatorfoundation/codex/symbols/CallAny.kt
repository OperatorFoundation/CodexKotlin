package org.operatorfoundation.codex.symbols

import java.math.BigInteger

class CallAny : Symbol {
    companion object {
        private val charToValue = mapOf(
            "A" to BigInteger.valueOf(0), "B" to BigInteger.valueOf(1), "C" to BigInteger.valueOf(2),
            "D" to BigInteger.valueOf(3), "E" to BigInteger.valueOf(4), "F" to BigInteger.valueOf(5),
            "G" to BigInteger.valueOf(6), "H" to BigInteger.valueOf(7), "I" to BigInteger.valueOf(8),
            "J" to BigInteger.valueOf(9), "K" to BigInteger.valueOf(10), "L" to BigInteger.valueOf(11),
            "M" to BigInteger.valueOf(12), "N" to BigInteger.valueOf(13), "O" to BigInteger.valueOf(14),
            "P" to BigInteger.valueOf(15), "Q" to BigInteger.valueOf(16), "R" to BigInteger.valueOf(17),
            "S" to BigInteger.valueOf(18), "T" to BigInteger.valueOf(19), "U" to BigInteger.valueOf(20),
            "V" to BigInteger.valueOf(21), "W" to BigInteger.valueOf(22), "X" to BigInteger.valueOf(23),
            "Y" to BigInteger.valueOf(24), "Z" to BigInteger.valueOf(25), "0" to BigInteger.valueOf(26),
            "1" to BigInteger.valueOf(27), "2" to BigInteger.valueOf(28), "3" to BigInteger.valueOf(29),
            "4" to BigInteger.valueOf(30), "5" to BigInteger.valueOf(31), "6" to BigInteger.valueOf(32),
            "7" to BigInteger.valueOf(33), "8" to BigInteger.valueOf(34), "9" to BigInteger.valueOf(35),
            " " to BigInteger.valueOf(36)
        )

        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
    }

    override fun size(): Int = 26 + 10 + 1

    override fun toString(): String = "CallAny"

    override fun decode(encodedValue: ByteArray): BigInteger {
        return charToValue[encodedValue.decodeToString()]
            ?: throw IllegalArgumentException("CallAny, bad value: $encodedValue")
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Unknown value $numericValue for CallAny"))
            .toByteArray()
    }
}