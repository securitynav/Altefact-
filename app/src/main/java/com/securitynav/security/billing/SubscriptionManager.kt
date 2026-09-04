package com.securitynav.security.billing

import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class SubscriptionTier { FREE, PRO_5USD, ULTIMATE_10USD }

object SubscriptionManager {

    private var currentTier = MutableStateFlow(SubscriptionTier.FREE)
    
    val isProUser = MutableStateFlow(false)

    fun getActiveTier(): SubscriptionTier {
        return currentTier.value
    }
    
    fun setActiveTier(tier: SubscriptionTier) {
        currentTier.value = tier
        isProUser.value = tier == SubscriptionTier.PRO_5USD || tier == SubscriptionTier.ULTIMATE_10USD
    }

    fun isFeatureAllowed(requiredTier: SubscriptionTier): Boolean {
        val tier = currentTier.value
        return when (requiredTier) {
            SubscriptionTier.FREE -> true
            SubscriptionTier.PRO_5USD -> tier == SubscriptionTier.PRO_5USD || tier == SubscriptionTier.ULTIMATE_10USD
            SubscriptionTier.ULTIMATE_10USD -> tier == SubscriptionTier.ULTIMATE_10USD
        }
    }

    fun initGooglePlayBilling(context: Context, onSuccess: () -> Unit) {
        val billingClient = BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        validatePurchaseOnRenderServer(purchase.purchaseToken, purchase.products.first())
                    }
                }
            }
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onSuccess()
                }
            }
            override fun onBillingServiceDisconnected() {}
        })
    }

    fun validatePurchaseOnRenderServer(token: String, productId: String) {
        if (productId.contains("pro")) {
            setActiveTier(SubscriptionTier.PRO_5USD)
        } else if (productId.contains("ultimate")) {
            setActiveTier(SubscriptionTier.ULTIMATE_10USD)
        }
    }
}
