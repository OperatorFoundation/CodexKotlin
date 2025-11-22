package org.operatorfoundation.codex.symbols
import android.telecom.Call
import java.math.BigInteger
import org.operatorfoundation.codex.*

class WSPRMessage(
    val prefix: Required,
    val callsign1: CallLetterNumber,
    val callsign2: CallLetterNumber,
    val callsign3: CallLetterNumber,
    val callsign4: CallLetterNumber,
    val callsign5: CallLetterNumber,
    val callsign6: CallLetterNumber,
    val grid1: GridLetter,
    val grid2: GridLetter,
    val grid3: GridNumber,
    val grid4: GridNumber,
    val power: Power
) : Symbol {
    companion object : SymbolFactory<WSPRMessage> {
        override fun size(): Int = (CallLetterNumber.size() * 6) + (GridLetter.size() * 2) + (GridNumber.size() * 2) + Power.size()

        override fun encode(numericValue: BigInteger): WSPRMessage {
            var remaining = numericValue

            // power
            var size = Power.size()
            var value = remaining.mod(size.toBigInteger())
            val power = Power.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // grid4
            size = GridNumber.size()
            value = remaining.mod(size.toBigInteger())
            val grid4 = GridNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // grid3
            size = GridNumber.size()
            value = remaining.mod(size.toBigInteger())
            val grid3 = GridNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // grid2
            size = GridLetter.size()
            value = remaining.mod(size.toBigInteger())
            val grid2 = GridLetter.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // grid1
            size = GridLetter.size()
            value = remaining.mod(size.toBigInteger())
            val grid1 = GridLetter.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // callsign6
            size = CallLetterNumber.size()
            value = remaining.mod(size.toBigInteger())
            val callsign6 = CallLetterNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // callsign5
            size = CallLetterNumber.size()
            value = remaining.mod(size.toBigInteger())
            val callsign5 = CallLetterNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // callsign4
            size = CallLetterNumber.size()
            value = remaining.mod(size.toBigInteger())
            val callsign4 = CallLetterNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // callsign3
            size = CallLetterNumber.size()
            value = remaining.mod(size.toBigInteger())
            val callsign3 = CallLetterNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // callsign2
            size = CallLetterNumber.size()
            value = remaining.mod(size.toBigInteger())
            val callsign2 = CallLetterNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            // callsign1
            size = CallLetterNumber.size()
            value = remaining.mod(size.toBigInteger())
            val callsign1 = CallLetterNumber.encode(value)
            remaining = remaining.divide(size.toBigInteger())

            require(remaining == BigInteger.ZERO) { "Value $numericValue is too large to encode in WSPR" }

            val required = Required('Q')

            return WSPRMessage(required, callsign1, callsign2, callsign3, callsign4, callsign5, callsign6, grid1, grid2, grid3, grid4, power)
        }
    }

    override fun toString(): String = "WSPRMessage(${prefix.value}, " +
            "${callsign1.value}${callsign2.value}${callsign3.value}${callsign4.value}${callsign5.value}${callsign6.value}, " +
            "${grid1.value}${grid2.value}${grid3.value}${grid4.value}, ${power.value})"

    override fun decode(): BigInteger {
        var result = BigInteger.ZERO

        // Process symbols in order (most significant first in mixed-radix)
        var size = CallLetterNumber.size()
        var decoded = callsign1.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = CallLetterNumber.size()
        decoded = callsign2.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = CallLetterNumber.size()
        decoded = callsign3.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = CallLetterNumber.size()
        decoded = callsign4.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = CallLetterNumber.size()
        decoded = callsign5.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = CallLetterNumber.size()
        decoded = callsign6.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = GridLetter.size()
        decoded = grid1.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = GridLetter.size()
        decoded = grid2.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = GridNumber.size()
        decoded = grid3.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = GridNumber.size()
        decoded = grid4.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        size = Power.size()
        decoded = power.decode()
        result = result.multiply(size.toBigInteger()).add(decoded)

        return result
    }
}