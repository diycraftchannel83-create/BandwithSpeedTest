package com.winsmediatama.speedtest

import android.annotation.SuppressLint
import android.os.Bundle\nimport com.google.android.gms.ads.MobileAds
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
    private var speedTestWebView: WebView? = null\n    private lateinit var appOpenAdManager: AppOpenAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Color(0xFF35D7F2),
                background = Color(0xFF081426),
                surface = Color(0xFF12243A)
            )) {
                SpeedTestWebScreen(
                    onWebViewCreated = { speedTestWebView = it }
                )
            }
        }
    }

    @Deprecated("Deprecated in Android")
    override fun onBackPressed() {
        val webView = speedTestWebView
        if (webView?.canGoBack() == true) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        speedTestWebView?.apply {
            stopLoading()
            destroy()
        }
        speedTestWebView = null
        super.onDestroy()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun SpeedTestWebScreen(onWebViewCreated: (WebView) -> Unit) {
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
