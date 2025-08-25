package org.operatorfoundation.codex.symbols

class Number : Symbol {
    override fun size(): Int {
        return 10
    }

    override fun toString(): String {
        return "Number"
    }

    override fun decode(encodedValue: ByteArray): Int {
        return when (encodedValue.toString()) {
            "0" -> 0
            "1" -> 1
            "2" -> 2
            "3" -> 3
            "4" -> 4
            "5" -> 5
            "6" -> 6
            "7" -> 7
            "8" -> 8
            "9" -> 9
            else -> throw IllegalArgumentException("Number, bad value: $encodedValue")
        }
    }

    override fun encode(numericValue: Int): ByteArray {
        return when (numericValue) {
            0 -> "0".toByteArray()
            1 -> "1".toByteArray()
            2 -> "2".toByteArray()
            3 -> "3".toByteArray()
            4 -> "4".toByteArray()
            5 -> "5".toByteArray()
            6 -> "6".toByteArray()
            7 -> "7".toByteArray()
            8 -> "8".toByteArray()
            9 -> "9".toByteArray()
            else -> throw IllegalArgumentException("Unknown value $numericValue for Number")
        }
    }
}