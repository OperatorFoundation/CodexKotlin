package org.operatorfoundation.codex

internal object WSPRMessageConverter
{
    /**
     * Parses encoded symbols into a structured WSPR message.
     *
     * Symbol mapping:
     * - encodedSymbols[0]: Required 'Q' (ignored)
     * - encodedSymbols[1-6]: Callsign (6 characters)
     * - encodedSymbols[7-10]: Grid square (4 characters)
     * - encodedSymbols[11]: Power level (2-character representation)
     */
    fun parseEncodedSymbolsToMessage(encodedSymbols: List<ByteArray>): WSPRDataMessage
    {
        val symbolList = WSPRCodex.getSymbolList()
        require(encodedSymbols.size == symbolList.size)
        {
            "Invalid encoded symbol count: ${encodedSymbols.size}, expected: ${symbolList.size}"
        }

        // Extract callsign (symbols 1-6)
        val callsign = buildString {
            for (i in 1..6)
            {
                append(encodedSymbols[i].decodeToString())
            }
        }

        // Extract grid square (symbols 7-10)
        val gridSquare = buildString {
            for (i in 7..10)
            {
                append(encodedSymbols[i].decodeToString())
            }
        }

        // Extract power level (symbol 11)
        val powerDbm = encodedSymbols[11].decodeToString().toInt()

        return WSPRDataMessage(callsign, gridSquare, powerDbm)
    }

    /**
     * Converts a WSPR message back to encoded symbol format for decoding.
     *
     * This reverses the parseEncodedSymbolsToMessage operation, reconstructing
     * the ByteArray list that the encoder originally produced.
     */
    fun convertMessageToEncodedSymbols(message: WSPRDataMessage): List<ByteArray>
    {
        val symbols = mutableListOf<ByteArray>()

        // Add the required prefix
        symbols.add("Q".toByteArray())

        // Add callsign characters (exactly 6 characters)
        val paddedCallsign = message.callsign.padEnd(6, ' ')
        require(paddedCallsign.length == 6)
        {
            "Callsign must be 6 characters or less: ${message.callsign}"
        }

        paddedCallsign.forEach { char ->
            symbols.add(char.toString().toByteArray())
        }

        // Add grid square characters (exactly 4 characters)
        require(message.gridSquare.length == 4)
        {
            "Grid square must be exactly 4 characters: ${message.gridSquare}"
        }

        message.gridSquare.forEach { char ->
            symbols.add(char.toString().toByteArray())
        }

        // Add power level
        symbols.add(message.powerDbm.toString().toByteArray())

        return symbols
    }
}