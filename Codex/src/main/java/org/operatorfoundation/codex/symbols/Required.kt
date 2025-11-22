package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class Required(val value: Char) : Symbol, SymbolFactory<Required> {
    override fun size(): Int = 0

    override fun toString(): String = "Required($value)"

    override fun encode(numericValue: BigInteger): Required {
        return Required(value)
    }

    override fun decode(): BigInteger {
        return BigInteger.ZERO
    }
}