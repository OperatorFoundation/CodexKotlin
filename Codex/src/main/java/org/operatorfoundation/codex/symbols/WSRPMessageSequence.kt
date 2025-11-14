package org.operatorfoundation.codex.symbols

import java.math.BigInteger
import org.operatorfoundation.codex.Encoder

/**
 * A sequence of WSPR messages that can encode arbitrarily large integers
 * by splitting them across multiple WSPR transmissions.
 *
 * This enables the Tidbit protocol to send messages larger than a single
 * WSPR message can contain by chaining multiple messages together.
 *
 * @param count Number of WSPR messages in the sequence
 */
class WSPRMessageSequence(private val count: Int) : Symbol {
    init {
        require(count > 0) { "Sequence must contain at least one WSPR message" }
    }

    private val wsrpMessages: List<Symbol> = List(count) { WSPRMessage() }
    private val encoder = Encoder(wsrpMessages)
    private val decoder = encoder.decoder()

    override fun size(): Int = wsrpMessages.sumOf { it.size() }

    override fun toString(): String = "WSPRMessageSequence($count)"

    override fun decode(encodedValue: ByteArray): BigInteger {
        require(encodedValue.size == size()) {
            "Encoded value must be ${size()} bytes for $count messages, got ${encodedValue.size}"
        }

        // Split the encoded byte array into parts for each WSPR message
        val parts = mutableListOf<ByteArray>()
        var offset = 0

        for (message in wsrpMessages) {
            val messageSize = message.size()
            val messageBytes = encodedValue.sliceArray(offset until offset + messageSize)
            parts.add(messageBytes)
            offset += messageSize
        }

        // Use the decoder to convert the parts back to an integer
        return decoder.decode(parts)
    }

    override fun encode(numericValue: BigInteger): ByteArray {
        // Use the encoder to convert the integer to a list of byte arrays
        val parts = encoder.encode(numericValue)

        require(parts.size == count) {
            "Encoder produced ${parts.size} parts but expected $count"
        }

        // Concatenate all parts into a single byte array
        return parts.flatMap { it.toList() }.toByteArray()
    }
}