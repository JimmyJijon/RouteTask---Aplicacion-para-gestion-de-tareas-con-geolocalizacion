package com.example.proyectofinal.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.data.entity.Tarea
import com.example.proyectofinal.data.entity.EstadoTarea
import com.example.proyectofinal.ui.viewmodel.TareaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTareaScreen(
    viewModel: TareaViewModel,
    volver: () -> Unit,
    navController: NavController
) {

    // Obtengo valores temporales para mantenerlos al navegar al mapa
    val tempTituloState by viewModel.tempTitulo.collectAsState()
    val tempDescripcionState by viewModel.tempDescripcion.collectAsState()
    val tempLat by viewModel.tempLat.collectAsState()
    val tempLon by viewModel.tempLon.collectAsState()

    var titulo by remember { mutableStateOf(tempTituloState) }
    var descripcion by remember { mutableStateOf(tempDescripcionState) }

    // Sincronizar el estado local con el ViewModel
    LaunchedEffect(tempTituloState) {
        titulo = tempTituloState
    }
    LaunchedEffect(tempDescripcionState) {
        descripcion = tempDescripcionState
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Nueva Tarea") },
                navigationIcon = {
                    IconButton(onClick = volver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            TextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                    viewModel.setTempFormulario(it, descripcion)
                },
                label = { Text("Título de la tarea") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    viewModel.setTempFormulario(titulo, it)
                },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Botón para ir al mapa
            Button(
                onClick = { navController.navigate("mapa") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar ubicación en mapa")
            }

            Spacer(Modifier.height(10.dp))

            // Muestro las coordenadas actuales
            Text("Latitud: ${tempLat?.toString() ?: "No seleccionada"}")
            Text("Longitud: ${tempLon?.toString() ?: "No seleccionada"}")

            Spacer(Modifier.height(20.dp))

            // Botón para guardar la tarea
            Button(
                onClick = {
                    if (titulo.isBlank() || descripcion.isBlank()) {
                        Toast.makeText(context, "Por favor, completa el título y la descripción", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.agregarTarea(
                        Tarea(
                            titulo = titulo,
                            descripcion = descripcion,
                            fecha = System.currentTimeMillis(),
                            latitud = tempLat,
                            longitud = tempLon,
                            estado = EstadoTarea.PENDIENTE.estado // El estado inicial es PENDIENTE
                        )
                    )
                    volver()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Tarea")
            }
        }
    }
}