package org.operatorfoundation.codex.symbols
import java.math.BigInteger
import org.operatorfoundation.codex.*

class PackedWSPRMessage(
    val b0: Octet,
    val b1: Octet,
    val b2: Octet,
    val b3: Octet,
    val b4: Octet,
    val b5: Octet,
    val b6: Quartet,
    val b7: Zero,
    val b8: Zero,
    val b9: Zero,
    val b10: Zero,
) : Symbol {
    companion object : SymbolFactory<PackedWSPRMessage>
    {
        fun fromByteAray(bs: ByteArray): PackedWSPRMessage
        {
            require(bs.size == 11) { "PackedWSPRMessage must be 11 bytes" }

            return PackedWSPRMessage(
                Octet(bs[0].toUByte().toInt()),
                Octet(bs[1].toUByte().toInt()),
                Octet(bs[2].toUByte().toInt()),
                Octet(bs[3].toUByte().toInt()),
                Octet(bs[4].toUByte().toInt()),
                Octet(bs[5].toUByte().toInt()),
                Quartet(bs[6].toUByte().toInt()),
                Zero(bs[7].toUByte().toInt()),
                Zero(bs[8].toUByte().toInt()),
                Zero(bs[9].toUByte().toInt()),
                Zero(bs[10].toUByte().toInt()),
            )
        }

        override fun size(): BigInteger {
            return Octet.size() *
                    Octet.size() *
                    Octet.size() *
                    Octet.size() *
                    Octet.size() *
                    Octet.size() *
                    Quartet.size()
        }

        override fun encode(numericValue: BigInteger): PackedWSPRMessage {
            var remaining = numericValue

            // b1
            var size = Octet.size()
            var value = remaining.mod(size)
            val b1 = Octet.encode(value)
            remaining = remaining.divide(size)

            // b2
            size = Octet.size()
            value = remaining.mod(size)
            val b2 = Octet.encode(value)
            remaining = remaining.divide(size)

            // b3
            size = Octet.size()
            value = remaining.mod(size)
            val b3 = Octet.encode(value)
            remaining = remaining.divide(size)

            // b4
            size = Octet.size()
            value = remaining.mod(size)
            val b4 = Octet.encode(value)
            remaining = remaining.divide(size)

            // b5
            size = Octet.size()
            value = remaining.mod(size)
            val b5 = Octet.encode(value)
            remaining = remaining.divide(size)

            // b6
            size = Octet.size()
            value = remaining.mod(size)
            val b6 = Octet.encode(value)
            remaining = remaining.divide(size)

            // b7
            size = Quartet.size()
            value = remaining.mod(size)
            val b7 = Quartet.encode(value)
            remaining = remaining.divide(size)

            require(remaining == BigInteger.ZERO) { "Value $numericValue is too large to encode in PackedWSPRMessage" }

            return PackedWSPRMessage(b1, b2, b3, b4, b5, b6, b7, Zero(0), Zero(0), Zero(0), Zero(0))
        }
    }

    override fun toString(): String = "PackedWSPRMessage(${bytes})"

    override fun decode(): BigInteger {
        var result = BigInteger.ZERO
        var multiplier = BigInteger.ONE

        // Process in same order as encode (LSB first)
        result = result.add(b0.decode().multiply(multiplier))
        multiplier = multiplier.multiply(Octet.size())

        result = result.add(b1.decode().multiply(multiplier))
        multiplier = multiplier.multiply(Octet.size())

        result = result.add(b2.decode().multiply(multiplier))
        multiplier = multiplier.multiply(Octet.size())

        result = result.add(b3.decode().multiply(multiplier))
        multiplier = multiplier.multiply(Octet.size())

        result = result.add(b4.decode().multiply(multiplier))
        multiplier = multiplier.multiply(Octet.size())

        result = result.add(b5.decode().multiply(multiplier))
        multiplier = multiplier.multiply(Octet.size())

        result = result.add(b6.decode().multiply(multiplier))

        return result
    }

    val bytes: ByteArray get() =
        byteArrayOf(
            b0.toByte(),
            b1.toByte(),
            b2.toByte(),
            b3.toByte(),
            b4.toByte(),
            b5.toByte(),
            b6.toByte(),
            b7.toByte(),
            b8.toByte(),
            b9.toByte(),
            b10.toByte()
        )
}