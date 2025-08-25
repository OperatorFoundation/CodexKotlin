package org.operatorfoundation.codex.symbols

class Power : Symbol {
    override fun size(): Int
    {
        return 19
    }

    override fun toString(): String {
        return "Power"
    }

    override fun decode(encodedValue: ByteArray): Int {
        return when (encodedValue.toString()) {
            "0" -> 0
            "3" -> 1
            "7" -> 2
            "10" -> 3
            "13" -> 4
            "17" -> 5
            "20" -> 6
            "23" -> 7
            "27" -> 8
            "30" -> 9
            "33" -> 10
            "37" -> 11
            "40" -> 12
            "43" -> 13
            "47" -> 14
            "50" -> 15
            "53" -> 16
            "57" -> 17
            "60" -> 18
            else -> throw IllegalArgumentException("Power, bad value $encodedValue")
        }
    }

    override fun encode(numericValue: Int): ByteArray {
        return when (numericValue) {
            0 -> "0".toByteArray()
            1 -> "3".toByteArray()
            2 -> "7".toByteArray()
            3 -> "10".toByteArray()
            4 -> "13".toByteArray()
            5 -> "17".toByteArray()
            6 -> "20".toByteArray()
            7 -> "23".toByteArray()
            8 -> "27".toByteArray()
            9 -> "30".toByteArray()
            10 -> "33".toByteArray()
            11 -> "37".toByteArray()
            12 -> "40".toByteArray()
            13 -> "43".toByteArray()
            14 -> "47".toByteArray()
            15 -> "50".toByteArray()
            16 -> "53".toByteArray()
            17 -> "57".toByteArray()
            18 -> "60".toByteArray()
            else -> throw IllegalArgumentException("Invalid value $numericValue for Power")
        }
    }
}