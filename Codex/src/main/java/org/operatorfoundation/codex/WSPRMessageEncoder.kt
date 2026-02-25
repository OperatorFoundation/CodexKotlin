package org.operatorfoundation.Codex

import org.operatorfoundation.codex.symbols.WSPRMessageSequence
import timber.log.Timber
import java.math.BigInteger

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
 * Finds the minimum number of WSPR messages needed to represent the data by
 * starting with a low estimate and incrementing until encoding succeeds.
 *
 * @param data Binary data to encode (typically encrypted message bytes)
 * @return List of [WSPRMessageFields] ready for frequency encoding, or null if
 *         encoding fails after [MAX_ENCODING_ATTEMPTS] tries
 */
fun encodeDataToWSPRMessages(data: ByteArray): List<WSPRMessageFields>?
{
    try
    {
        Timber.d("=== WSPR Encoding ===")
        Timber.d("Input: ${data.size} bytes")
        Timber.d("Hex: ${data.joinToString("") { "%02x".format(it) }}")

        // Convert encrypted bytes to unsigned BigInteger
        val numericValue = BigInteger(1, data)

        Timber.d("BigInteger bits: ${numericValue.bitLength()}")

        // Estimate starting message count (~50 bits capacity per WSPR message)
        val estimatedMessages = maxOf(1, (numericValue.bitLength() / 50) + 1)
        var messageCount = estimatedMessages

        while (messageCount <= MAX_ENCODING_ATTEMPTS)
        {
            try
            {
                val encoded = WSPRMessageSequence.encode(numericValue)
                val fields = encoded.toWSPRFields()

                Timber.d("Encoded ${data.size} bytes into ${fields.size} WSPR message(s)")
                return fields
            }
            catch (e: Exception)
            {
                Timber.d("Encoding failed with $messageCount message(s): ${e.message}")
                messageCount++
            }
        }

        Timber.e("Could not encode data within $MAX_ENCODING_ATTEMPTS attempts")
        return null
    }
    catch (e: Exception)
    {
        Timber.e(e, "Error encoding data to WSPR messages")
        return null
    }
}

private const val MAX_ENCODING_ATTEMPTS = 20