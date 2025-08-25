package org.operatorfoundation.Codex.Symbols

class Required(private val r: Any) {
    val length: Int
        get() = 1

    override fun toString(): String {
        return "Required($r)"
    }

    fun encode(n: Any): Any {
        return r
    }

    fun decode(n: Any) {
        if (n != r) {
            throw IllegalArgumentException("Required($r) != $n")
        }
    }
}