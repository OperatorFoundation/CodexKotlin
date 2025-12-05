package org.operatorfoundation.codex.symbols
import java.math.BigInteger
import org.operatorfoundation.codex.*

/**
 * A sequence of WSPR messages that can encode arbitrarily large integers
 * by splitting them across multiple WSPR transmissions.
 */
class PackedWSPRMessageSequence(val messages: List<PackedWSPRMessage>) : Symbol {
    companion object : SymbolFactory<PackedWSPRMessageSequence> {
        override fun size(): BigInteger {
            throw UnsupportedOperationException("PackedWSPRMessageSequence does not have a fixed size - it depends on the number of messages in the sequence")
        }

        override fun encode(numericValue: BigInteger): PackedWSPRMessageSequence {
            val messages = mutableListOf<PackedWSPRMessage>()
            var remaining = numericValue
            val messageSize = PackedWSPRMessage.size() // Use WSPRMessage.size(), NOT size()!

            do {
                val value = remaining.mod(messageSize)
                val message = PackedWSPRMessage.encode(value)
                messages.add(message)
                remaining = remaining.divide(messageSize)
            } while (remaining > BigInteger.ZERO)

            return PackedWSPRMessageSequence(messages)
        }
    }

    override fun toString(): String = "PackedWSPRMessageSequence(${messages.size})"

    override fun decode(): BigInteger {
        var result = BigInteger.ZERO
        val messageSize = PackedWSPRMessage.size() // Use PackedWSPRMessage.size(), NOT size()!

        // Process messages in order with positional values (least significant first)
        var multiplier = BigInteger.ONE
        for(message in messages) {
            val decoded = message.decode()
            result = result.add(decoded.multiply(multiplier))
            multiplier = multiplier.multiply(messageSize)
        }

        return result
    }

    fun extractValues(): List<ByteArray>
    {
        return messages.map { it.bytes }
    }
}