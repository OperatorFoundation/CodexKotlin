package org.operatorfoundation.codex.symbols

import java.math.BigInteger

class Binary : Symbol {
    override fun size(): Int = 2

    override fun toString(): String {
        return "Binary"
    }

    override fun decode(encodedValue: ByteArray): BigInteger {
        return when (encodedValue.decodeToString()) {
            "0" -> 0
            "1" -> 1
            else -> throw IllegalArgumentException("Binary, bad value $encodedValue")
        }
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        return when (numericValue) {
            0 -> "0".toByteArray()
            1 -> "1".toByteArray()
            else -> throw IllegalArgumentException("Unknown value $numericValue for Binary")
        }
    }
}