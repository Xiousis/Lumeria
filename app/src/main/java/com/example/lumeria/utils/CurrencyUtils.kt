package com.example.lumeria.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val formatter = NumberFormat.getInstance(Locale.US)

    fun formatGold(amount: Int): String {
        return "${formatter.format(amount)}G"
    }

    fun formatNumber(amount: Int): String {
        return formatter.format(amount)
    }

    fun getDiscountMultiplier(upgradeLevel: Int): Double {
        return 1.0 - (upgradeLevel * 0.01)
    }

    fun applyDiscount(amount: Int, upgradeLevel: Int, activeLawId: Int? = null): Int {
        var mult = getDiscountMultiplier(upgradeLevel)
        if (activeLawId == 1) mult *= 0.85 // Merchant's Subsidy: -15%
        return (amount * mult).toInt()
    }
}
