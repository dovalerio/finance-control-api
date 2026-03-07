package com.heitor.finance.domain.valueobject

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Money(val amount: BigDecimal) {

    init {
        require(amount >= BigDecimal.ZERO) { "Amount must be non-negative" }
    }

    val isZero: Boolean get() = amount.compareTo(BigDecimal.ZERO) == 0

    operator fun plus(other: Money): Money = Money(amount.add(other.amount))
    operator fun minus(other: Money): Money = Money(amount.subtract(other.amount).max(BigDecimal.ZERO))

    override fun toString(): String = amount.setScale(2, RoundingMode.HALF_UP).toPlainString()

    companion object {
        val ZERO = Money(BigDecimal.ZERO)
        fun of(value: BigDecimal): Money = Money(value)
        fun of(value: String): Money = Money(BigDecimal(value))
    }
}
