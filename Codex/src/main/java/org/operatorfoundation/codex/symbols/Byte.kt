package org.operatorfoundation.codex.symbols

/**
 * Byte symbol - encodes/decodes byte values (0-255).
 * Used for binary data encoding.
 */
class Byte : Symbol {
    override fun size() = 256
    override fun toString() = "Byte"

    override fun encode(numericValue: Int): ByteArray {
        if (numericValue < 0 || numericValue > 255) {
            throw Exception("Invalid value $numericValue for Byte (must be 0-255)")
        }
        return numericValue.toChar().toString().toByteArray()
    }

    override fun decode(encodedValue: ByteArray): Int {
        if (encodedValue.size != 1)
        {
            throw Exception("Too many bytes")
        }

        return encodedValue[0].toInt()
    }
}
