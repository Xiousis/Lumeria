package com.solerforge.lumeria.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val formatter = NumberFormat.getInstance(Locale.US)

    fun formatGold(amount: Long): String {
        return "${formatter.format(amount)}G"
    }

    fun formatGold(amount: Int): String {
        return formatGold(amount.toLong())
    }

    fun formatNumber(amount: Long): String {
        return formatter.format(amount)
    }

    fun formatNumber(amount: Int): String {
        return formatNumber(amount.toLong())
    }

    fun getDiscountMultiplier(upgradeLevel: Int): Double {
        return 1.0 - (upgradeLevel * 0.01)
    }

    fun applyDiscount(amount: Long, upgradeLevel: Int, activeLawId: Int? = null): Long {
        var mult = getDiscountMultiplier(upgradeLevel)
        if (activeLawId == 1) mult *= 0.85 // Merchant's Subsidy: -15%
        return (amount * mult).toLong()
    }

    fun applyDiscount(amount: Int, upgradeLevel: Int, activeLawId: Int? = null): Int {
        return applyDiscount(amount.toLong(), upgradeLevel, activeLawId).toInt()
    }
}
