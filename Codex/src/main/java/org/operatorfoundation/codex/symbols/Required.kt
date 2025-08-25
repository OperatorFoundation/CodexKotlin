package org.operatorfoundation.codex.symbols

class Required(private val r: kotlin.Byte) : Symbol {
    override fun size(): Int {
        return 1
    }

    override fun toString(): String {
        return "Required($r)"
    }

    override fun encode(numericValue: Int): ByteArray {
        return byteArrayOf(r)
    }

    override fun decode(encodedValue: ByteArray): Int {
        if (encodedValue.size != 1) {
            throw IllegalArgumentException("Required($r) != $encodedValue")
        }

        if (encodedValue[0] != r)
        {
            throw IllegalArgumentException("Required($r) != $encodedValue")
        }

        return r.toInt()
    }
}