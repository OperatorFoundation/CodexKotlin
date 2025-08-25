package org.operatorfoundation.codex.symbols

class CallAny : Symbol {
    companion object {
        private val charToValue = mapOf(
            "A" to 0, "B" to 1, "C" to 2, "D" to 3, "E" to 4, "F" to 5,
            "G" to 6, "H" to 7, "I" to 8, "J" to 9, "K" to 10, "L" to 11,
            "M" to 12, "N" to 13, "O" to 14, "P" to 15, "Q" to 16, "R" to 17,
            "S" to 18, "T" to 19, "U" to 20, "V" to 21, "W" to 22, "X" to 23,
            "Y" to 24, "Z" to 25, "0" to 26, "1" to 27, "2" to 28, "3" to 29,
            "4" to 30, "5" to 31, "6" to 32, "7" to 33, "8" to 34, "9" to 35,
            " " to 36
        )

        private val valueToChar = charToValue.map { (k, v) -> v to k }.toMap()
    }

    override fun size(): Int = 26 + 10 + 1

    override fun toString(): String = "CallAny"

    override fun decode(encodedValue: ByteArray): Int {
        return charToValue[encodedValue.toString()]
            ?: throw IllegalArgumentException("CallAny, bad value: $encodedValue")
    }

    override fun encode(numericValue: Int): ByteArray {
        return (valueToChar[numericValue]
            ?: throw IllegalArgumentException("Unknown value $numericValue for CallAny"))
            .toByteArray()
    }
}