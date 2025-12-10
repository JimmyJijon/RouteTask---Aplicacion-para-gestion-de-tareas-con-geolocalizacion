package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ui.viewmodel.TareaViewModel
import com.example.proyectofinal.data.entity.EstadoTarea
import com.example.proyectofinal.data.entity.Tarea
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen(
    viewModel: TareaViewModel,
    irAgregar: () -> Unit,
    irEditar: (Int) -> Unit,
    irVerUbicacion: (Int, Double, Double, String) -> Unit
) {
    val tareas = viewModel.tareas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Tareas") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = irAgregar) {
                Text("+")
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(tareas.value) { tarea ->
                TareaItem(
                    tarea = tarea,
                    onClick = { irEditar(tarea.id) },
                    onDelete = { viewModel.eliminarTarea(tarea) },
                    onVerUbicacion = {
                        if (tarea.latitud != null && tarea.longitud != null) {
                            irVerUbicacion(tarea.id, tarea.latitud, tarea.longitud, tarea.titulo)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun TareaItem(
    tarea: Tarea,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onVerUbicacion: () -> Unit
) {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaTexto = formato.format(Date(tarea.fecha))

    // Función para asignar un color basado en el estado de la tarea
    fun obtenerColorEstado(estado: String): Color {
        return when (estado) {
            EstadoTarea.REALIZADA.estado -> Color(0xFF4CAF50) // Verde para Realizada
            EstadoTarea.CANCELADA.estado -> Color(0xFFF44336) // Rojo para Cancelada
            else -> Color(0xFFFFA726) // Naranja para Pendiente
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Muestro el estado con un color distintivo
            Text(
                text = "Estado: ${tarea.estado}",
                style = MaterialTheme.typography.titleSmall,
                color = obtenerColorEstado(tarea.estado)
            )
            Spacer(Modifier.height(4.dp))

            Text(text = tarea.titulo, style = MaterialTheme.typography.titleLarge)
            Text(text = tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha de creación: $fechaTexto", style = MaterialTheme.typography.bodySmall)

            if (tarea.latitud != null && tarea.longitud != null) {
                Text(
                    text = "Ubicación: ${tarea.latitud}, ${tarea.longitud}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Ver ubicación solo si tiene coordenadas
                if (tarea.latitud != null && tarea.longitud != null) {
                    Button(
                        onClick = onVerUbicacion,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ver ubicación en mapa")
                    }
                }

                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}