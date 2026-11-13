package com.winsmediatama.speedtest

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

/**
 * Loads App Open ads ahead of time and displays them when the app returns
 * to the foreground. Debug builds use Google's test ad unit.
 */
class AppOpenAdManager(context: Context) {
    private val appContext = context.applicationContext
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var adLoadedAtMillis = 0L

    private fun isAdFresh(): Boolean =
        appOpenAd != null && System.currentTimeMillis() - adLoadedAtMillis < 4 * 60 * 60 * 1000L

    private val adUnitId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/9257395921"
        } else {
            "ca-app-pub-1254774552944426/5183216826"
        }

    fun loadAd() {
        if (isLoadingAd || isAdFresh()) return
        appOpenAd = null
        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            appContext,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    adLoadedAtMillis = System.currentTimeMillis()
                    isLoadingAd = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    appOpenAd = null
                    isLoadingAd = false
                }
            }
        )
    }

    fun showAdIfAvailable(activity: Activity) {
        if (isShowingAd) return
        val ad = if (isAdFresh()) appOpenAd else null
        if (ad == null) {
            appOpenAd = null
            loadAd()
            return
        }

        isShowingAd = true
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                appOpenAd = null
                isShowingAd = false
                loadAd()
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }
        ad.show(activity)
    }
}
