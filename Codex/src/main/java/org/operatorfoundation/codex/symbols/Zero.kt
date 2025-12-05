package org.operatorfoundation.codex.symbols
import org.operatorfoundation.codex.Symbol
import org.operatorfoundation.codex.SymbolFactory
import java.math.BigInteger

class Zero(value: Int) : Symbol
{
    companion object: SymbolFactory<Zero>
    {
        override fun size(): BigInteger = 0.toBigInteger()

        override fun toString(): String = "Zero"

        override fun encode(numericValue: BigInteger): Zero {
            require(numericValue == 0.toBigInteger()) { "Zero value must be 0" }

            return Zero(0)
        }
    }

    init
    {
        require(value == 0) { "Zero value must be 0" }
    }

    override fun decode(): BigInteger {
        return BigInteger.ZERO
    }

    fun toByte(): Byte
    {
        return 0
    }
}