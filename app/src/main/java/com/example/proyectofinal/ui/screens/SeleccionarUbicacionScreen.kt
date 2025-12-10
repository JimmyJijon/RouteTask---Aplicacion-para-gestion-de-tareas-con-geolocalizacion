package com.example.proyectofinal.ui.screens

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ui.viewmodel.TareaViewModel
import com.example.proyectofinal.web.JsBridge

@Composable
fun SeleccionarUbicacionScreen(
    viewModel: TareaViewModel,
    volver: () -> Unit,
    initialLat: Double? = null,
    initialLon: Double? = null
) {
    val context = LocalContext.current
    var seleccionLat by remember { mutableStateOf<Double?>(null) }
    var seleccionLon by remember { mutableStateOf<Double?>(null) }
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    Column(Modifier.fillMaxSize()) {

        AndroidView(factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (initialLat != null && initialLon != null) {
                            view?.evaluateJavascript("setInitialPosition($initialLat, $initialLon);", null)
                        }
                    }
                }

                // Actualizado con callback de volver
                addJavascriptInterface(
                    JsBridge(
                        listener = { lat, lon ->
                            this.post {
                                seleccionLat = lat
                                seleccionLon = lon
                                // Guardar y volver automáticamente
                                viewModel.setTempUbicacion(lat, lon)
                                volver()
                            }
                        },
                        onBack = {
                            post { volver() }
                        }
                    ),
                    "AndroidInterface"
                )

                loadUrl("file:///android_asset/leaflet_map.html")
                webViewRef.value = this
            }
        }, modifier = Modifier.weight(1f))

        // Mostrar coords seleccionadas y botones
        Column(Modifier.padding(12.dp)) {
            Text("Latitud: ${seleccionLat ?: "No seleccionado"}")
            Text("Longitud: ${seleccionLon ?: "No seleccionado"}")

            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (seleccionLat != null && seleccionLon != null) {
                        viewModel.setTempUbicacion(seleccionLat!!, seleccionLon!!)
                        volver()
                    }
                }, modifier = Modifier.weight(1f)) {
                    Text("Usar ubicación seleccionada")
                }

                Button(onClick = { volver() }, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
            }
        }
    }
}