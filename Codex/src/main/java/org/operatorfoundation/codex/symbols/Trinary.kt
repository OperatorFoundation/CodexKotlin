package org.operatorfoundation.codex.symbols

class Trinary : Symbol {
    override fun size(): Int
    {
        return 3
    }

    override fun toString(): String {
        return "Trinary"
    }

    override fun decode(encodedValue: ByteArray): Int {
        return when (encodedValue.toString()) {
            "0" -> 0
            "1" -> 1
            "2" -> 2
            else -> throw IllegalArgumentException("Trinary, bad value $encodedValue")
        }
    }

    override fun encode(numericValue: Int): ByteArray {
        return when (numericValue) {
            0 -> "0".toByteArray()
            1 -> "1".toByteArray()
            2 -> "2".toByteArray()
            else -> throw IllegalArgumentException("Unknown value $numericValue for Trinary")
        }
    }
}