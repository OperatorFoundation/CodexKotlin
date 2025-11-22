package org.operatorfoundation.codex.symbols
import java.math.BigInteger
import org.operatorfoundation.codex.*
import kotlin.reflect.full.companionObjectInstance

/**
 * A sequence of WSPR messages that can encode arbitrarily large integers
 * by splitting them across multiple WSPR transmissions.
 */
class WSPRMessageSequence(val messages: List<WSPRMessage>) : Symbol {
    companion object : SymbolFactory<WSPRMessageSequence> {
        override fun size(): Int {
            val callsign = CallLetterNumber::class.companionObjectInstance as? SymbolFactory<*>
            if (callsign == null) {
                return 0
            }
            val callSignSize = callsign.size()

            val gridLetter = GridLetter::class.companionObjectInstance as? SymbolFactory<*>
            if (gridLetter == null) {
                return 0
            }
            val gridLetterSize = gridLetter.size()

            val gridNumber = GridNumber::class.companionObjectInstance as? SymbolFactory<*>
            if (gridNumber == null) {
                return 0
            }
            val gridNumberSize = gridNumber.size()

            val power = Power::class.companionObjectInstance as? SymbolFactory<*>
            if (power == null) {
                return 0
            }
            val powerSize = power.size()

            return (callSignSize * 6) + (gridLetterSize * 2) + (gridNumberSize * 2) + powerSize
        }

        override fun encode(numericValue: BigInteger): WSPRMessageSequence {
            val messages = mutableListOf<WSPRMessage>()
            var remaining = numericValue
            val messageSize = size().toBigInteger()

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
        val messageSize = size().toBigInteger()

        for(message in messages) {
            // Process symbols in order (most significant first in mixed-radix)
            val decoded = message.decode()
            result = result.multiply(messageSize).add(decoded)
        }

        return result
    }
}