package org.operatorfoundation.codex.symbols

import java.math.BigInteger

/**
 * Byte symbol - encodes/decodes byte values (0-255).
 * Used for binary data encoding.
 */
class Byte : Symbol
{
    override fun size() = 256
    override fun toString() = "Byte"

    override fun encode(numericValue: BigInteger): ByteArray
    {
        if (numericValue < BigInteger.ZERO || numericValue > BigInteger.valueOf(255))
        {
            throw IllegalArgumentException("Invalid value $numericValue for Byte (must be 0-255)")
        }

        // A single raw byte
        return byteArrayOf(numericValue.toInt().toByte())
    }

    override fun decode(encodedValue: ByteArray): BigInteger
    {
        if (encodedValue.size != 1)
        {
            throw IllegalArgumentException("Invalid encoded value length: ${encodedValue.size}  ")
        }

        // unsigned 0–255
        return encodedValue[0].toUByte().toInt().toBigInteger()
    }
}
