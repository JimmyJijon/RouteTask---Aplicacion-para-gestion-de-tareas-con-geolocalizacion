package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectofinal.data.entity.EstadoTarea
import com.example.proyectofinal.ui.viewmodel.TareaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTareaScreen(
    viewModel: TareaViewModel,
    id: Int,
    volver: () -> Unit,
    navController: NavController
) {
    val tareas = viewModel.tareas.collectAsState()
    val tarea = tareas.value.firstOrNull { it.id == id }

    if (tarea == null) {
        Text("Cargando la tarea...")
        return
    }

    // Si vuelvo del mapa, estos valores no serán vacíos
    val tempTituloState by viewModel.tempTitulo.collectAsState()
    val tempDescripcionState by viewModel.tempDescripcion.collectAsState()

    // Inicializo los campos: uso el valor temporal si existe, sino, el valor original de la tarea
    var titulo by remember { mutableStateOf(
        if (tempTituloState.isNotEmpty()) tempTituloState else tarea.titulo
    ) }
    var descripcion by remember { mutableStateOf(
        if (tempDescripcionState.isNotEmpty()) tempDescripcionState else tarea.descripcion
    ) }

    // Sincronizar el estado local con el ViewModel
    LaunchedEffect(tempTituloState, tempDescripcionState) {
        if (tempTituloState.isNotEmpty()) titulo = tempTituloState
        if (tempDescripcionState.isNotEmpty()) descripcion = tempDescripcionState
    }

    var mensajeError by remember { mutableStateOf("") }

    var estadoSeleccionado by remember { mutableStateOf(tarea.estado) }
    var expandirMenu by remember { mutableStateOf(false) }

    val tempLat by viewModel.tempLat.collectAsState()
    val tempLon by viewModel.tempLon.collectAsState()

    val latitudActual = tempLat ?: tarea.latitud
    val longitudActual = tempLon ?: tarea.longitud

    //

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Tarea: ${tarea.titulo}") },
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


            // Selector para cambiar el estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandirMenu = true }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${estadoSeleccionado}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Cambiar estado")

                DropdownMenu(
                    expanded = expandirMenu,
                    onDismissRequest = { expandirMenu = false }
                ) {
                    EstadoTarea.values().forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado.estado) },
                            onClick = {
                                estadoSeleccionado = estado.estado
                                expandirMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            TextField(
                value = titulo,
                onValueChange = {
                    titulo = it
                    mensajeError = ""
                    viewModel.setTempFormulario(it, descripcion) // <-- ¡GUARDAR CAMBIO EN EL VM!
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Título de la tarea") }
            )

            Spacer(Modifier.height(16.dp))

            TextField(
                value = descripcion,
                onValueChange = {
                    descripcion = it
                    mensajeError = ""
                    viewModel.setTempFormulario(titulo, it) // <-- ¡GUARDAR CAMBIO EN EL VM!
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción") }
            )

            Spacer(Modifier.height(20.dp))

            //  boton para cambiar unicacion
            Button(
                onClick = {
                    // El VM ya tiene los datos del formulario gracias a los onValueChange
                    if (latitudActual != null && longitudActual != null) {
                        viewModel.setTempUbicacion(latitudActual, longitudActual)
                    }
                    navController.navigate("mapa")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cambiar ubicación en mapa")
            }

            Spacer(Modifier.height(10.dp))

            // Mostrar ubicación actual
            Text("Latitud: ${latitudActual?.toString() ?: "No seleccionada"}")
            Text("Longitud: ${longitudActual?.toString() ?: "No seleccionada"}")

            Spacer(Modifier.height(16.dp))

            // Muestro mensajes de error si hay
            if (mensajeError.isNotEmpty()) {
                Text(
                    text = mensajeError,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Button(
                onClick = {
                    if (titulo.isBlank() || descripcion.isBlank()) {
                        mensajeError = "Asegúrate de que el título y la descripción no estén vacíos."
                    } else {
                        viewModel.actualizarTarea(
                            tarea.copy(
                                titulo = titulo,
                                descripcion = descripcion,
                                latitud = latitudActual,
                                longitud = longitudActual,
                                estado = estadoSeleccionado
                            )
                        )
                        // limpiar estados antes de guardar y volver
                        viewModel.limpiarTempUbicacion()
                        viewModel.limpiarTempFormulario()
                        volver()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar cambios")
            }
        }
    }
}