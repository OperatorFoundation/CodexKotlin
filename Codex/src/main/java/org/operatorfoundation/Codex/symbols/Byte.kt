import org.operatorfoundation.Codex.Symbols.Symbol

/**
 * Byte symbol - encodes/decodes byte values (0-255).
 * Used for binary data encoding.
 */
class Byte : Symbol {
    override fun size() = 256
    override fun toString() = "Byte"

    override fun encode(numericValue: Int): Int {
        if (numericValue < 0 || numericValue > 255) {
            throw Exception("Invalid value $numericValue for Byte (must be 0-255)")
        }
        return numericValue
    }

    override fun decode(encodedValue: Any): Int {
        val value = when (encodedValue) {
            is Int -> encodedValue
            is kotlin.Byte -> encodedValue.toInt() and 0xFF  // Handle signed bytes properly
            is Char -> encodedValue.code  // ASCII value of character
            else -> throw Exception("Invalid type for Byte decode: ${encodedValue::class}")
        }
        if (value < 0 || value > 255) {
            throw Exception("Invalid value $value for Byte (must be 0-255)")
        }
        return value
    }
}
