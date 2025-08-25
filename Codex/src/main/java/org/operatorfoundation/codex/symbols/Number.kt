package org.operatorfoundation.codex.Symbols

class Number {
    val length: Int
        get() = 10

    override fun toString(): String {
        return "Number"
    }

    fun decode(n: ByteArray): Int {
        return when (n) {
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
            else -> throw IllegalArgumentException("Number, bad value: $n")
        }
    }

    fun encode(n: Int): ByteArray {
        return when (n) {
            0 -> "0"
            1 -> "1"
            2 -> "2"
            3 -> "3"
            4 -> "4"
            5 -> "5"
            6 -> "6"
            7 -> "7"
            8 -> "8"
            9 -> "9"
            else -> throw IllegalArgumentException("Unknown value $n for Number")
        }
    }
}