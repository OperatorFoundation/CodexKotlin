package org.operatorfoundation.Codex.Symbols

class Byte {
    val length: Int
        get() = 256

    override fun toString(): String {
        return "Byte"
    }

    fun encode(n: Int): Int {
        if (n < 0) {
            throw IllegalArgumentException("Invalid value $n for Byte")
        }
        if (n > 255) {
            throw IllegalArgumentException("Invalid value $n for Byte")
        }
        return n
    }

    fun decode(n: Int): Int {
        if (n < 0) {
            throw IllegalArgumentException("Invalid value $n for Byte")
        }
        if (n > 255) {
            throw IllegalArgumentException("Invalid value $n for Byte")
        }
        return n
    }
}