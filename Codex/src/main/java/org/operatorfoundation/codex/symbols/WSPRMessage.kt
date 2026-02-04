package org.operatorfoundation.codex.symbols
import java.math.BigInteger
import org.operatorfoundation.codex.*

class WSPRMessage(
    val callsign0: CallLetter,      // Letter (position 1)
    val callsign1: CallLetter,      // Letter (position 2)
    val callsign2: CallNumber,      // Number (position 3) - MUST be a digit
    val callsign3: CallLetter,      // Letter (position 4)
    val callsign4: CallLetter,      // Letter (position 5)
    val callsign5: CallLetter,      // Letter (position 6)
    val grid1: GridLetter,
    val grid2: GridLetter,
    val grid3: GridNumber,
    val grid4: GridNumber,
    val power: Power
) : Symbol {
    companion object : SymbolFactory<WSPRMessage> {
        override fun size(): BigInteger {
            val callLetterCapacity = BigInteger.valueOf(CallLetter.size().toLong()).pow(5)  // 5 letter positions
            val callNumberCapacity = CallNumber.size()  // 1 number position
            val gridLetterCapacity = BigInteger.valueOf(GridLetter.size().toLong()).pow(2)
            val gridNumberCapacity = BigInteger.valueOf(GridNumber.size().toLong()).pow(2)
            val powerCapacity = Power.size()

            return callLetterCapacity
                .multiply(callNumberCapacity)
                .multiply(gridLetterCapacity)
                .multiply(gridNumberCapacity)
                .multiply(powerCapacity)
        }

        override fun encode(numericValue: BigInteger): WSPRMessage {
            var remaining = numericValue

            // power
            var size = Power.size()
            var value = remaining.mod(size)
            val power = Power.encode(value)
            remaining = remaining.divide(size)

            // grid4
            size = GridNumber.size()
            value = remaining.mod(size)
            val grid4 = GridNumber.encode(value)
            remaining = remaining.divide(size)

            // grid3
            size = GridNumber.size()
            value = remaining.mod(size)
            val grid3 = GridNumber.encode(value)
            remaining = remaining.divide(size)

            // grid2
            size = GridLetter.size()
            value = remaining.mod(size)
            val grid2 = GridLetter.encode(value)
            remaining = remaining.divide(size)

            // grid1
            size = GridLetter.size()
            value = remaining.mod(size)
            val grid1 = GridLetter.encode(value)
            remaining = remaining.divide(size)

            // callsign5 - Letter
            size = CallLetter.size()
            value = remaining.mod(size)
            val callsign5 = CallLetter.encode(value)
            remaining = remaining.divide(size)

            // callsign4 - Letter
            size = CallLetter.size()
            value = remaining.mod(size)
            val callsign4 = CallLetter.encode(value)
            remaining = remaining.divide(size)

            // callsign3 - Letter
            size = CallLetter.size()
            value = remaining.mod(size)
            val callsign3 = CallLetter.encode(value)
            remaining = remaining.divide(size)

            // callsign2 - NUMBER position
            size = CallNumber.size()
            value = remaining.mod(size)
            val callsign2 = CallNumber.encode(value)
            remaining = remaining.divide(size)

            // callsign1 - Letter
            size = CallLetter.size()
            value = remaining.mod(size)
            val callsign1 = CallLetter.encode(value)
            remaining = remaining.divide(size)

            // callsign0 - Letter (most significant)
            size = CallLetter.size()
            value = remaining.mod(size)
            val callsign0 = CallLetter.encode(value)
            remaining = remaining.divide(size)

            require(remaining == BigInteger.ZERO) { "Value $numericValue is too large to encode in WSPR" }

            return WSPRMessage(callsign0, callsign1, callsign2, callsign3, callsign4, callsign5, grid1, grid2, grid3, grid4, power)
        }

        /**
         * Creates a WSPRMessage from WSPR transmission fields.
         *
         * This is the inverse of toWSPRFields() - it reconstructs a WSPRMessage
         * from the callsign, grid square, and power level received from a WSPR decode.
         *
         * @param callsign The 6-character callsign (e.g., "QA0BCD") - must start with 'Q'
         * @param gridSquare The 4-character grid square (e.g., "FN31")
         * @param powerDbm The power level in dBm (must be a valid WSPR power level)
         * @return WSPRMessage instance
         * @throws IllegalArgumentException if any parameter is invalid
         */
        fun fromWSPRFields(callsign: String, gridSquare: String, powerDbm: Int): WSPRMessage
        {
            // Validate and parse callsign
            val trimmedCallsign = callsign.trim().uppercase()

            require(trimmedCallsign.length == 6) {
                "Callsign must be exactly 6 characters, got: '$callsign' (${trimmedCallsign.length})"
            }

            // Validate and parse grid square
            val trimmedGrid = gridSquare.trim().uppercase()
            require(trimmedGrid.length == 4) {
                "Grid square must be exactly 4 characters, got: '$gridSquare' (${trimmedGrid.length})"
            }

            // Build the WSPRMessage from parsed values
            val callsign0 = CallLetter.fromChar(trimmedCallsign[0])
            val callsign1 = CallLetter.fromChar(trimmedCallsign[1])
            val callsign2 = CallNumber.fromChar(trimmedCallsign[2])
            val callsign3 = CallLetter.fromChar(trimmedCallsign[3])
            val callsign4 = CallLetter.fromChar(trimmedCallsign[4])
            val callsign5 = CallLetter.fromChar(trimmedCallsign[5])

            val grid0 = GridLetter.fromChar(trimmedGrid[0])
            val grid1 = GridLetter.fromChar(trimmedGrid[1])
            val grid2 = GridNumber.fromChar(trimmedGrid[2])
            val grid3 = GridNumber.fromChar(trimmedGrid[3])

            val power = Power.fromDbm(powerDbm)

            return WSPRMessage(
                callsign0, callsign1, callsign2, callsign3, callsign4, callsign5,
                grid0, grid1, grid2, grid3, power
            )
        }
    }

    override fun toString(): String = "WSPRMessage(" +
            "${callsign0.value}${callsign1.value}${callsign2.value}${callsign3.value}${callsign4.value}${callsign5.value}, " +
            "${grid1.value}${grid2.value}${grid3.value}${grid4.value}, ${power.value})"

    override fun decode(): BigInteger {
        var result = BigInteger.ZERO

        // callsign0 - Letter (most significant)
        var size = CallLetter.size()
        var decoded = callsign0.decode()
        result = result.multiply(size).add(decoded)

        // callsign1
        size = CallLetter.size()
        decoded = callsign1.decode()
        result = result.multiply(size).add(decoded)

        // callsign2 - NUMBER position
        size = CallNumber.size()
        decoded = callsign2.decode()
        result = result.multiply(size).add(decoded)

        // callsign3
        size = CallLetter.size()
        decoded = callsign3.decode()
        result = result.multiply(size).add(decoded)

        // callsign4
        size = CallLetter.size()
        decoded = callsign4.decode()
        result = result.multiply(size).add(decoded)

        // callsign5
        size = CallLetter.size()
        decoded = callsign5.decode()
        result = result.multiply(size).add(decoded)

        // grid1
        size = GridLetter.size()
        decoded = grid1.decode()
        result = result.multiply(size).add(decoded)

        // grid2
        size = GridLetter.size()
        decoded = grid2.decode()
        result = result.multiply(size).add(decoded)

        // grid3
        size = GridNumber.size()
        decoded = grid3.decode()
        result = result.multiply(size).add(decoded)

        // grid4
        size = GridNumber.size()
        decoded = grid4.decode()
        result = result.multiply(size).add(decoded)

        // power (least significant)
        size = Power.size()
        decoded = power.decode()
        result = result.multiply(size).add(decoded)

        return result
    }

    fun toWSPRFields(): Triple<String, String, Int>
    {
        val callsign = "${callsign0.value}${callsign1.value}${callsign2.value}${callsign3.value}${callsign4.value}${callsign5.value}"
        val grid = "${grid1.value}${grid2.value}${grid3.value}${grid4.value}"
        val power = power.value
        return Triple(callsign, grid, power)
    }
}