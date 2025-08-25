package org.operatorfoundation.Codex.Symbols

class Binary {
    val length: Int
        get() = 2

    override fun toString(): String {
        return "Binary"
    }

    fun decode(n: String): Int {
        return when (n) {
            "0" -> 0
            "1" -> 1
            else -> throw IllegalArgumentException("Binary, bad value $n")
        }
    }

    fun encode(n: Int): String {
        return when (n) {
            0 -> "0"
            1 -> "1"
            else -> throw IllegalArgumentException("Unknown value $n for Binary")
        }
    }
}