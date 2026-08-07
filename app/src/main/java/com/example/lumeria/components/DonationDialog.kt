package com.example.lumeria.components

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.ProductDetails

@Composable
fun DonationDialog(
    products: List<ProductDetails>,
    onPurchase: (Activity, ProductDetails) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Support the Developer", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("If you're enjoying Lumeria, consider buying a coffee! It helps keep the updates coming.")
                
                if (products.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    products.sortedBy { it.oneTimePurchaseOfferDetails?.priceAmountMicros }.forEach { product ->
                        val price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "???"
                        RpgButton(
                            text = "${product.name}: $price",
                            onClick = {
                                if (activity != null) {
                                    onPurchase(activity, product)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color(0xFF8D6E63)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Cyan)
            }
        },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray
    )
}
