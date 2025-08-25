package org.operatorfoundation.Codex.Symbols

class Trinary {
    val length: Int
        get() = 3

    override fun toString(): String {
        return "Trinary"
    }

    fun decode(n: String): Int {
        return when (n) {
            "0" -> 0
            "1" -> 1
            "2" -> 2
            else -> throw IllegalArgumentException("Trinary, bad value $n")
        }
    }

    fun encode(n: Int): String {
        return when (n) {
            0 -> "0"
            1 -> "1"
            2 -> "2"
            else -> throw IllegalArgumentException("Unknown value $n for Trinary")
        }
    }
}