package com.example.proyectofinal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectofinal.ui.screens.ListaTareasScreen
import com.example.proyectofinal.ui.screens.AgregarTareaScreen
import com.example.proyectofinal.ui.screens.EditarTareaScreen
import com.example.proyectofinal.ui.screens.MapScreen
import com.example.proyectofinal.ui.screens.VerUbicacionScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: com.example.proyectofinal.ui.viewmodel.TareaViewModel
) {
    NavHost(navController = navController, startDestination = "lista") {

        // En tu NavHost
        composable("lista") {
            ListaTareasScreen(
                viewModel = viewModel,
                irAgregar = { navController.navigate("agregar") },
                irEditar = { tareaId -> navController.navigate("editar/$tareaId") },
                irVerUbicacion = { tareaId, lat, lon, titulo ->
                    navController.navigate("verUbicacion/$tareaId/$lat/$lon/$titulo")
                }
            )
        }

        composable("verUbicacion/{tareaId}/{lat}/{lon}/{titulo}") { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
            val titulo = backStackEntry.arguments?.getString("titulo") ?: ""

            if (lat != null && lon != null) {
                VerUbicacionScreen(
                    latitud = lat,
                    longitud = lon,
                    titulo = titulo,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        //  AGREGAR
        composable("agregar") {
            AgregarTareaScreen(
                viewModel = viewModel,
                navController = navController,   // <-- IMPORTANTE
                volver = { navController.popBackStack() }
            )
        }

        // EDITAR

        composable("editar/{tareaId}") { backStackEntry ->
            val tareaId = backStackEntry.arguments?.getString("tareaId")?.toIntOrNull()
            if (tareaId != null) {
                EditarTareaScreen(
                    viewModel = viewModel,
                    id = tareaId,
                    volver = { navController.popBackStack() },
                    navController = navController  // Agregar este parámetro
                )
            }
        }

        // MAPA (Leaflet)
        composable("mapa") {
            val tempLat by viewModel.tempLat.collectAsState()
            val tempLon by viewModel.tempLon.collectAsState()

            MapScreen(
                onLocationSelected = { lat, lon ->
                    viewModel.setTempUbicacion(lat, lon)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                initialLat = tempLat,
                initialLon = tempLon
            )
        }
    }
}
