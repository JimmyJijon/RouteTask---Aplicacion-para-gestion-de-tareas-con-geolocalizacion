package com.example.proyectofinal.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyectofinal.web.JsBridge

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(
    onLocationSelected: (Double, Double) -> Unit,
    onNavigateBack: () -> Unit,
    initialLat: Double? = null, // <- Debe coincidir con el nombre y tipo
    initialLon: Double? = null  // <- Debe coincidir con el nombre y tipo
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                val settings = this.settings

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.builtInZoomControls = true
                settings.displayZoomControls = false

                WebView.setWebContentsDebuggingEnabled(true)

                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        Log.e("WebView", "Error $errorCode: $description en $failingUrl")
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        Log.d("WebView", "Página cargada: $url")
                    }
                }

                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        Log.d("WebView", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                        return true
                    }
                }

                // Pasar ambos callbacks al JsBridge
                addJavascriptInterface(
                    JsBridge(
                        listener = { lat, lon ->
                            onLocationSelected(lat, lon)
                        },
                        onBack = {
                            post { onNavigateBack() }
                        }
                    ),
                    "AndroidInterface"
                )

                loadUrl("file:///android_asset/leaflet_map.html")
            }
        }
    )
}