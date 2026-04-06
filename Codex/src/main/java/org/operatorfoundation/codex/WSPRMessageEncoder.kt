package org.operatorfoundation.codex

import org.operatorfoundation.codex.symbols.WSPRMessage
import org.operatorfoundation.codex.symbols.WSPRMessageSequence
import timber.log.Timber
import java.math.BigInteger

private const val MAX_ENCODING_ATTEMPTS = 20

/**
 * Number of bits reserved in spot 0's numeric value for the unencrypted
 * message header. The header encodes the total spot count N, allowing the
 * receiver to know exactly how many spots to accumulate before decoding.
 *
 * 6 bits gives a range of 0–63, sufficient for MAX_UNENCRYPTED_SPOTS.
 * If MAX_UNENCRYPTED_SPOTS is raised above 63, increase HEADER_BITS accordingly.
 */
private const val HEADER_BITS = 6

/**
 * Maximum number of WSPR spots supported for a single unencrypted message.
 * Must be less than 2^HEADER_BITS (currently 64).
 */
const val MAX_UNENCRYPTED_SPOTS = 30


/**
 * Represents a single decoded WSPR message ready for frequency encoding.
 *
 * Replaces the Triple<String, String, Int> previously used in FriendInfoActivity.
 */
data class WSPRMessageFields(
    val callsign: String,
    val gridSquare: String,
    val powerDbm: Int
)

/**
 * Encodes binary data into a sequence of WSPR messages.
 *
 * Converts the data to an unsigned BigInteger and encodes it using
 * WSPRMessageSequence, which determines the minimum number of WSPR
 * messages needed to represent the value.
 *
 * @param data Binary data to encode (typically encrypted message bytes)
 * @return List of [WSPRMessageFields] ready for frequency encoding, or null if encoding fails
 */
fun encodeDataToWSPRMessages(data: ByteArray): List<WSPRMessageFields>?
{
    return try
    {
        Timber.d("=== WSPR Encoding ===")
        Timber.d("Input: ${data.size} bytes")
        Timber.d("Hex: ${data.joinToString("") { "%02x".format(it) }}")

        val numericValue = BigInteger(1, data)
        Timber.d("BigInteger bits: ${numericValue.bitLength()}")

        val encoded = WSPRMessageSequence.encode(numericValue)
        Timber.d("Encoded ${data.size} bytes into ${encoded.messages.size} WSPR message(s)")

        encoded.toWSPRFields()
    }
    catch (e: Exception)
    {
        Timber.e(e, "Error encoding data to WSPR messages")
        null
    }
}

/**
 * Encodes plaintext bytes for unencrypted WSPR transmission.
 *
 * Spot 0's numeric value is split into two fields:
 *   - Upper field (header): total spot count N, packed into the top HEADER_BITS bits
 *   - Lower field (payload chunk): first chunk of the payload BigInteger
 *
 * Spots 1..N-1 carry the remainder of the payload in standard mixed-radix
 * encoding (least significant first), same as WSPRMessageSequence's normal encoding.
 *
 * The receiver extracts N from spot 0 on first arrival, then waits until
 * receivedMessages.size == N (total spots including spot 0) before decoding.
 *
 * @param plaintext Raw UTF-8 bytes of the message
 * @return List of WSPRMessageFields ready for transmission, or null if encoding fails
 */
fun encodeUnencryptedPayload(plaintext: ByteArray): List<WSPRMessageFields>?
{
    return try
    {
        val messageSize = WSPRMessage.size()
        val headerRange = BigInteger.valueOf(1L shl HEADER_BITS)

        // C is the payload capacity of spot 0. The upper HEADER_BITS bits are
        // reserved for the spot count header, leaving floor(messageSize / headerRange)
        // values available for the first payload chunk.
        val C = messageSize / headerRange

        val payloadBigInt = BigInteger(1, plaintext)

        // Find the smallest N (total spots including spot 0) such that the full
        // payload fits within the available capacity: C * messageSize^(N-1) > payloadBigInt
        var N = 1
        var capacity = C
        while (capacity <= payloadBigInt)
        {
            N++
            if (N > MAX_UNENCRYPTED_SPOTS)
            {
                Timber.e("Unencrypted payload too large: ${plaintext.size} bytes requires more than $MAX_UNENCRYPTED_SPOTS spots")
                return null
            }
            capacity = capacity * messageSize
        }

        // Encode spot 0: pack header (N) and first payload chunk into one WSPRMessage value
        val spot0PayloadChunk = payloadBigInt.mod(C)
        val spot0Value = BigInteger.valueOf(N.toLong()) * C + spot0PayloadChunk
        val spot0 = WSPRMessage.encode(spot0Value)

        // Encode spots 1..N-1 from the residual payload using standard mixed-radix
        val messages = mutableListOf(spot0)
        var residual = payloadBigInt / C
        repeat(N - 1)
        {
            val spotValue = residual.mod(messageSize)
            messages.add(WSPRMessage.encode(spotValue))
            residual = residual / messageSize
        }

        Timber.d("Unencrypted encode: ${plaintext.size} bytes -> $N spots")
        messages.map { it.toWSPRFields() }
    }
    catch (e: Exception)
    {
        Timber.e(e, "Error encoding unencrypted payload")
        null
    }
}

/**
 * Attempts to decode accumulated WSPR messages as an unencrypted plaintext payload.
 *
 * Called by the receiver after each new spot arrives. Returns non-null only
 * when the full message is complete.
 *
 * Spot 0 encodes N (total expected spots including itself) in its upper bits.
 * Returns null if:
 *   - messages.size < N (still accumulating)
 *   - spot 0's header is malformed (N < 1 or N > MAX_UNENCRYPTED_SPOTS)
 *
 * @param messages Accumulated WSPRMessages in reception order (spot 0 first)
 * @return Decoded UTF-8 plaintext if complete, null if still accumulating or malformed
 */
fun tryDecodeUnencryptedPayload(messages: List<WSPRMessage>): String?
{
    if (messages.isEmpty()) return null

    return try
    {
        val messageSize = WSPRMessage.size()
        val headerRange = BigInteger.valueOf(1L shl HEADER_BITS)
        val C = messageSize / headerRange

        // Extract N from the upper bits of spot 0
        val spot0Value = messages[0].decode()
        val N = (spot0Value / C).toInt()

        // Discard malformed headers — N must be a valid spot count
        if (N < 1 || N > MAX_UNENCRYPTED_SPOTS)
        {
            Timber.w("Unencrypted decode: malformed header in spot 0 (N=$N), discarding")
            return null
        }

        // Not enough spots yet — caller should keep accumulating
        if (messages.size < N) return null

        // Reconstruct payload BigInteger from spot 0 chunk + spots 1..N-1
        val spot0PayloadChunk = spot0Value.mod(C)

        // Spots 1..N-1 carry the residual in standard mixed-radix (least significant first)
        var residual = BigInteger.ZERO
        var multiplier = BigInteger.ONE
        for (i in 1 until N)
        {
            residual = residual + messages[i].decode() * multiplier
            multiplier = multiplier * messageSize
        }

        val payloadBigInt = spot0PayloadChunk + C * residual

        // Convert to bytes, stripping potential BigInteger two's-complement sign byte
        val rawBytes = payloadBigInt.toByteArray()
        val payloadBytes = if (rawBytes.isNotEmpty() && rawBytes[0] == 0.toByte() && rawBytes.size > 1)
            rawBytes.copyOfRange(1, rawBytes.size)
        else
            rawBytes

        val result = String(payloadBytes, Charsets.UTF_8)
        Timber.d("Unencrypted decode: $N spots -> ${payloadBytes.size} bytes")
        result
    }
    catch (e: Exception)
    {
        // Not a warning — expected during normal accumulation
        Timber.d("Unencrypted decode attempt: ${e.message}")
        null
    }
}