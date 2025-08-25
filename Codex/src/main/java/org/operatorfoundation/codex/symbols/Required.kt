package org.operatorfoundation.codex.Symbols

class Required(private val r: Any) {
    val length: Int
        get() = 1

    override fun toString(): String {
        return "Required($r)"
    }

    fun encode(n: Int): ByteArray {
        return r
    }

    fun decode(n: ByteArray): Int {
        if (n != r) {
            throw IllegalArgumentException("Required($r) != $n")
        }
    }
}