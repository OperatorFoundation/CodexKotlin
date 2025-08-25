package org.operatorfoundation.codex.symbols

class CallLetter : Symbol {
    override fun size(): Int {
        return 26
    }

    override fun toString(): String {
        return "CallLetter"
    }

    override fun decode(encodedValue: ByteArray): Int {
        return when (encodedValue.toString()) {
            "A" -> 0
            "B" -> 1
            "C" -> 2
            "D" -> 3
            "E" -> 4
            "F" -> 5
            "G" -> 6
            "H" -> 7
            "I" -> 8
            "J" -> 9
            "K" -> 10
            "L" -> 11
            "M" -> 12
            "N" -> 13
            "O" -> 14
            "P" -> 15
            "Q" -> 16
            "R" -> 17
            "S" -> 18
            "T" -> 19
            "U" -> 20
            "V" -> 21
            "W" -> 22
            "X" -> 23
            "Y" -> 24
            "Z" -> 25
            else -> throw IllegalArgumentException("CallLetter, bad value: $encodedValue")
        }
    }

    override fun encode(numericValue: Int): ByteArray {
        return when (numericValue) {
            0 -> "A".toByteArray()
            1 -> "B".toByteArray()
            2 -> "C".toByteArray()
            3 -> "D".toByteArray()
            4 -> "E".toByteArray()
            5 -> "F".toByteArray()
            6 -> "G".toByteArray()
            7 -> "H".toByteArray()
            8 -> "I".toByteArray()
            9 -> "J".toByteArray()
            10 -> "K".toByteArray()
            11 -> "L".toByteArray()
            12 -> "M".toByteArray()
            13 -> "N".toByteArray()
            14 -> "O".toByteArray()
            15 -> "P".toByteArray()
            16 -> "Q".toByteArray()
            17 -> "R".toByteArray()
            18 -> "S".toByteArray()
            19 -> "T".toByteArray()
            20 -> "U".toByteArray()
            21 -> "V".toByteArray()
            22 -> "W".toByteArray()
            23 -> "X".toByteArray()
            24 -> "Y".toByteArray()
            25 -> "Z".toByteArray()
            else -> throw IllegalArgumentException("Unknown value $numericValue for CallLetter")
        }
    }
}