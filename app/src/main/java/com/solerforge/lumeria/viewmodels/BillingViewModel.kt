package com.solerforge.lumeria.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.solerforge.lumeria.billing.BillingManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class BillingViewModel(private val billingManager: BillingManager) : ViewModel() {

    val products = billingManager.products.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isReady = billingManager.isReady.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun makePurchase(activity: Activity, productDetails: ProductDetails) {
        billingManager.launchPurchaseFlow(activity, productDetails)
    }
}

class BillingViewModelFactory(private val billingManager: BillingManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BillingViewModel(billingManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
