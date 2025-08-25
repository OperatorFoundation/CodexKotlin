package org.operatorfoundation.Codex.Symbols

class GridLetter {
    val length: Int
        get() = 18

    override fun toString(): String {
        return "GridLetter"
    }

    fun decode(n: String): Int {
        return when (n) {
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
            else -> throw IllegalArgumentException("GridLetter, bad value $n")
        }
    }

    fun encode(n: Int): String {
        return when (n) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            4 -> "E"
            5 -> "F"
            6 -> "G"
            7 -> "H"
            8 -> "I"
            9 -> "J"
            10 -> "K"
            11 -> "L"
            12 -> "M"
            13 -> "N"
            14 -> "O"
            15 -> "P"
            16 -> "Q"
            17 -> "R"
            else -> throw IllegalArgumentException("Unknown value $n for GridLetter")
        }
    }
}