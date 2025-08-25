package org.operatorfoundation.codex.Symbols

class Power {
    val length: Int
        get() = 19

    override fun toString(): String {
        return "Power"
    }

    fun decode(n: ByteArray): Int {
        return when (n) {
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
            else -> throw IllegalArgumentException("Power, bad value $n")
        }
    }

    fun encode(n: Int): ByteArray {
        return when (n) {
            0 -> "0"
            1 -> "3"
            2 -> "7"
            3 -> "10"
            4 -> "13"
            5 -> "17"
            6 -> "20"
            7 -> "23"
            8 -> "27"
            9 -> "30"
            10 -> "33"
            11 -> "37"
            12 -> "40"
            13 -> "43"
            14 -> "47"
            15 -> "50"
            16 -> "53"
            17 -> "57"
            18 -> "60"
            else -> throw IllegalArgumentException("Invalid value $n for Power")
        }
    }
}