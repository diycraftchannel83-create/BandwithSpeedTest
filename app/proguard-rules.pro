# Preserve JavaScript bridge methods exposed to WebView JavaScript.
-keepclassmembers class com.winsmediatama.speedtest.AndroidAdsBridge {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve the bridge class name used by addJavascriptInterface.
-keep class com.winsmediatama.speedtest.AndroidAdsBridge { *; }
