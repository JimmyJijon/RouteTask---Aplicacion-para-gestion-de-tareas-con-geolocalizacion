package com.example.proyectofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.data.db.AppDatabase
import com.example.proyectofinal.data.repository.TareaRepository
import com.example.proyectofinal.ui.navigation.AppNavHost
import com.example.proyectofinal.ui.viewmodel.TareaViewModel
import com.example.proyectofinal.ui.viewmodel.TareaViewModelFactory
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // 1) Base de datos
        val db = AppDatabase.getDatabase(applicationContext)

        // 2) Repository
        val repo = TareaRepository(db.tareaDao())

        // 3) ViewModel factory
        val factory = TareaViewModelFactory(repo)

        // 4) ViewModel
        val viewModel = ViewModelProvider(this, factory)[TareaViewModel::class.java]

        setContent {
            ProyectoFinalTheme {
                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}



