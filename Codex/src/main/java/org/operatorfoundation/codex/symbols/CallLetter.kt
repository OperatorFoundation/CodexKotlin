package org.operatorfoundation.codex.symbols

class CallLetter : Symbol {
    companion object {
        private val charToValue = mapOf(
            "A" to 0, "B" to 1, "C" to 2, "D" to 3, "E" to 4, "F" to 5,
            "G" to 6, "H" to 7, "I" to 8, "J" to 9, "K" to 10, "L" to 11,
            "M" to 12, "N" to 13, "O" to 14, "P" to 15, "Q" to 16, "R" to 17,
            "S" to 18, "T" to 19, "U" to 20, "V" to 21, "W" to 22, "X" to 23,
            "Y" to 24, "Z" to 25
        )

        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
    }

    override fun size(): Int = 26

    override fun toString(): String = "CallLetter"

    override fun decode(encodedValue: ByteArray): Int {
        return charToValue[encodedValue.toString()]
            ?: throw IllegalArgumentException("CallLetter, bad value: $encodedValue")
    }

    override fun encode(numericValue: Int): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Unknown value $numericValue for CallLetter"))
            .toByteArray()
    }
}