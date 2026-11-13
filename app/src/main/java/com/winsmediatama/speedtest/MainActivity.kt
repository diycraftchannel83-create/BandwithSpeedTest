package com.winsmediatama.speedtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.gms.ads.MobileAds
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

private const val SPEED_TEST_URL = "https://bandwithspeedtest.diy-craft-channel-83.workers.dev/"

class MainActivity : ComponentActivity() {
    private var speedTestWebView: WebView? = null
    private lateinit var appOpenAdManager: AppOpenAdManager
    private lateinit var interstitialAdManager: InterstitialAdManager
    private var mobileAdsInitialized = false
    private var hasGoneToBackground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appOpenAdManager = AppOpenAdManager(this)
        interstitialAdManager = InterstitialAdManager(this)
        MobileAds.initialize(this) {
            mobileAdsInitialized = true
            appOpenAdManager.loadAd()
            interstitialAdManager.loadAd()
        }
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Color(0xFF35D7F2),
                background = Color(0xFF081426),
                surface = Color(0xFF12243A)
            )) {
                SpeedTestWebScreen(
                    onWebViewCreated = { speedTestWebView = it },
                    onRequestInterstitial = { showInterstitialThenStartTest() }
                )
            }
        }
    }

    private fun showInterstitialThenStartTest() {
        // If an ad is not ready, never block the speed test.
        if (!mobileAdsInitialized || !interstitialAdManager.isAdAvailable()) {
            interstitialAdManager.loadAd()
            startSpeedTestInWebView()
            return
        }
        interstitialAdManager.showAd(this) {
            startSpeedTestInWebView()
        }
    }

    private fun startSpeedTestInWebView() {
        runOnUiThread {
            speedTestWebView?.evaluateJavascript(
                "window.startSpeedTestFromAndroid && window.startSpeedTestFromAndroid();",
                null
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (hasGoneToBackground && mobileAdsInitialized) {
            appOpenAdManager.showAdIfAvailable(this)
        }
    }

    override fun onStop() {
        hasGoneToBackground = true
        super.onStop()
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        val webView = speedTestWebView
        if (webView?.canGoBack() == true) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        speedTestWebView?.apply {
            removeJavascriptInterface("AndroidAds")
            stopLoading()
            destroy()
        }
        speedTestWebView = null
        super.onDestroy()
    }
}

private class AndroidAdsBridge(
    private val onRequestInterstitial: () -> Unit
) {
    @JavascriptInterface
    fun requestInterstitial() {
        onRequestInterstitial()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun SpeedTestWebScreen(
    onWebViewCreated: (WebView) -> Unit,
    onRequestInterstitial: () -> Unit
) {
    var loading by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF081426)),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.loadsImagesAutomatically = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    addJavascriptInterface(AndroidAdsBridge(onRequestInterstitial), "AndroidAds")
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest
                        ): Boolean {
                            val host = request.url.host.orEmpty()
                            return !(host == "bandwithspeedtest.diy-craft-channel-83.workers.dev" ||
                                host == "speed.cloudflare.com" ||
                                host == "github.com" ||
                                host.endsWith(".pages.dev") ||
                                host == "cdn.jsdelivr.net")
                        }

                        override fun onPageFinished(view: WebView, url: String) {
                            loading = false
                        }
                    }
                    onWebViewCreated(this)
                    loadUrl(SPEED_TEST_URL)
                }
            }
        )
        if (loading) {
            CircularProgressIndicator(color = Color(0xFF35D7F2))
        }
    }
}
