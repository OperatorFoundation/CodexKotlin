package org.operatorfoundation.codex.symbols

import java.math.BigInteger

/**
 * Byte symbol - encodes/decodes byte values (0-255).
 * Used for binary data encoding.
 */
class Byte : Symbol {
    override fun size() = 256
    override fun toString() = "Byte"

    override fun encode(numericValue: BigInteger): ByteArray {
        if (numericValue < 0.toBigInteger() || numericValue > 255.toBigInteger()) {
            throw Exception("Invalid value $numericValue for Byte (must be 0-255)")
        }
        return numericValue.toInt().toChar().toString().toByteArray()
    }

    override fun decode(encodedValue: ByteArray): BigInteger {
        if (encodedValue.size != 1)
        {
            throw Exception("Too many bytes")
        }

        return encodedValue[0].toInt().toBigInteger()
    }
}
