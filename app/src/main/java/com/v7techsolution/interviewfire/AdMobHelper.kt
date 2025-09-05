package com.v7techsolution.interviewfire

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class AdMobHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "AdMobHelper"
        private const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9672937825247791/1003654851"
        private const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Test ad unit
    }
    
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false
    
    // Initialize AdMob
    fun initialize() {
        Log.d(TAG, "Initializing AdMob...")
        MobileAds.initialize(context) { initializationStatus ->
            Log.d(TAG, "AdMob initialization complete: ${initializationStatus.adapterStatusMap}")
        }
        loadInterstitialAd()
    }
    
    // Load interstitial ad
    private fun loadInterstitialAd() {
        if (isAdLoading || interstitialAd != null) {
            Log.d(TAG, "Ad is already loading or loaded")
            return
        }
        
        Log.d(TAG, "Loading interstitial ad...")
        isAdLoading = true
        
        val adRequest = AdRequest.Builder().build()
        
        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID, // Use real ad unit ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Failed to load interstitial ad: ${adError.message}")
                    interstitialAd = null
                    isAdLoading = false
                }
                
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully")
                    interstitialAd = ad
                    isAdLoading = false
                    setupAdCallbacks(ad)
                }
            }
        )
    }
    
    // Setup ad callbacks
    private fun setupAdCallbacks(ad: InterstitialAd) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Ad was clicked")
            }
            
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed full screen content")
                interstitialAd = null
                // Preload next ad
                loadInterstitialAd()
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Ad failed to show full screen content: ${adError.message}")
                interstitialAd = null
                // Preload next ad
                loadInterstitialAd()
            }
            
            override fun onAdImpression() {
                Log.d(TAG, "Ad recorded an impression")
            }
            
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed full screen content")
            }
        }
    }
    
    // Show interstitial ad
    fun showInterstitialAd(activity: Activity, onAdClosed: (() -> Unit)? = null) {
        if (interstitialAd != null) {
            Log.d(TAG, "Showing interstitial ad")
            interstitialAd?.show(activity)
        } else {
            Log.w(TAG, "Interstitial ad not ready, loading new ad")
            loadInterstitialAd()
            onAdClosed?.invoke() // Continue without ad if not available
        }
    }
    
    // Check if ad is ready
    fun isAdReady(): Boolean {
        return interstitialAd != null
    }
    
    // Preload ad (call this when you know an ad will be needed soon)
    fun preloadAd() {
        if (interstitialAd == null && !isAdLoading) {
            loadInterstitialAd()
        }
    }
}
