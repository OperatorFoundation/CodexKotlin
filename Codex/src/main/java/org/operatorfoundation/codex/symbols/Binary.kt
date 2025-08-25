package org.operatorfoundation.codex.symbols

class Binary : Symbol {
    override fun size(): Int = 2

    override fun toString(): String {
        return "Binary"
    }

    override fun decode(encodedValue: ByteArray): Int {
        return when (encodedValue) {
            "0" -> 0
            "1" -> 1
            else -> throw IllegalArgumentException("Binary, bad value $encodedValue")
        }
    }

    override fun encode(numericValue: Int): ByteArray {
        return when (numericValue) {
            0 -> "0"
            1 -> "1"
            else -> throw IllegalArgumentException("Unknown value $numericValue for Binary")
        }
    }
}