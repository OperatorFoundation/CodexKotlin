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

        /**
         * Creates a WSPRMessageSequence from WSPR transmission fields.
         *
         * This reconstructs a message sequence from a list of (callsign, grid, power) tuples
         * received from WSPR decodes. The messages must be provided in transmission order
         * (least significant first).
         *
         * @param fields List of triples containing (callsign, gridSquare, powerDbm)
         * @return WSPRMessageSequence instance
         * @throws IllegalArgumentException if any field is invalid
         */
        fun fromWSPRFields(fields: List<Triple<String, String, Int>>): WSPRMessageSequence
        {
            require(fields.isNotEmpty()) { "Cannot create WSPRMessageSequence from empty list" }

            val messages = fields.map { (callsign, grid, power) ->
                WSPRMessage.fromWSPRFields(callsign, grid, power)
            }

            return WSPRMessageSequence(messages)
        }

        /**
         * Creates a WSPRMessageSequence from a list of WSPRMessage objects.
         *
         * @param messages List of WSPRMessage objects in transmission order
         * @return WSPRMessageSequence instance
         */
        fun fromMessages(messages: List<WSPRMessage>): WSPRMessageSequence
        {
            require(messages.isNotEmpty()) { "Cannot create WSPRMessageSequence from empty list" }
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

    /**
     * Decodes the message sequence to a byte array.
     *
     * This is a convenience method for decoding encrypted message data.
     * The BigInteger is converted to a byte array suitable for decryption.
     *
     * @return ByteArray containing the decoded data
     */
    fun decodeToBytes(): ByteArray {
        val bigInt = decode()
        return bigInt.toByteArray()
    }

    fun toWSPRFields(): List<Triple<String, String, Int>>
    {
        return messages.map { it.toWSPRFields() }
    }
}