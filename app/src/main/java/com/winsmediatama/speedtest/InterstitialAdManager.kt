package com.winsmediatama.speedtest

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.LoadAdError

/**
 * Preloads interstitial ads. If no ad is ready, the caller starts the requested
 * speed test immediately instead of waiting for an ad.
 */
class InterstitialAdManager(context: Context) {
    private val appContext = context.applicationContext
    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    private var isShowing = false

    private val adUnitId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            "ca-app-pub-1254774552944426/3774492347"
        }

    fun isAdAvailable(): Boolean = interstitialAd != null && !isShowing

    fun loadAd() {
        if (isLoading || interstitialAd != null) return
        isLoading = true
        InterstitialAd.load(
            appContext,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isLoading = false
                }
            }
        )
    }

    fun showAd(activity: Activity, onFinished: () -> Unit) {
        val ad = interstitialAd
        if (ad == null || isShowing) {
            loadAd()
            onFinished()
            return
        }

        interstitialAd = null
        isShowing = true
        var continued = false
        fun finishOnce() {
            if (continued) return
            continued = true
            isShowing = false
            loadAd()
            onFinished()
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() = finishOnce()
            override fun onAdFailedToShowFullScreenContent(error: AdError) = finishOnce()
        }
        ad.show(activity)
    }
}
