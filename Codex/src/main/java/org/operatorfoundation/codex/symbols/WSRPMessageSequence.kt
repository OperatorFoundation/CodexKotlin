package org.operatorfoundation.codex.symbols
import java.math.BigInteger
import org.operatorfoundation.codex.*

/**
 * A sequence of WSPR messages that can encode arbitrarily large integers
 * by splitting them across multiple WSPR transmissions.
 */
class WSPRMessageSequence(val messages: List<WSPRMessage>) : Symbol {
    companion object : SymbolFactory<WSPRMessageSequence> {
        override fun size(): BigInteger {
            throw UnsupportedOperationException("WSPRMessageSequence does not have a fixed size - it depends on the number of messages in the sequence")
        }

        override fun encode(numericValue: BigInteger): WSPRMessageSequence {
            val messages = mutableListOf<WSPRMessage>()
            var remaining = numericValue
            val messageSize = WSPRMessage.size() // Use WSPRMessage.size(), NOT size()!

            do {
                val value = remaining.mod(messageSize)
                val message = WSPRMessage.encode(value)
                messages.add(message)
                remaining = remaining.divide(messageSize)
            } while (remaining > BigInteger.ZERO)

            return WSPRMessageSequence(messages)
        }
    }

    override fun toString(): String = "WSPRMessageSequence(${messages.size})"

    override fun decode(): BigInteger {
        var result = BigInteger.ZERO
        val messageSize = WSPRMessage.size() // Use WSPRMessage.size(), NOT size()!

        // Process messages in order with positional values (least significant first)
        var multiplier = BigInteger.ONE
        for(message in messages) {
            val decoded = message.decode()
            result = result.add(decoded.multiply(multiplier))
            multiplier = multiplier.multiply(messageSize)
        }

        return result
    }

    fun extractValues(): List<Triple<String, String, Int>>
    {
        return messages.map { it.extractValues() }
    }
}