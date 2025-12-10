package com.example.proyectofinal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.entity.Tarea
import com.example.proyectofinal.data.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(private val repo: TareaRepository) : ViewModel() {

    val tareas = repo.todasLasTareas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun agregarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repo.agregarTarea(tarea)
            // Limpio el estado temporal después de agregar
            limpiarTempUbicacion()
            limpiarTempFormulario()
        }
    }

    fun actualizarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repo.actualizarTarea(tarea)
            // Limpio el estado temporal después de actualizar, así evito que el estado
            // temporal afecte a otra tarea que se vaya a editar después.
            limpiarTempUbicacion()
            limpiarTempFormulario()
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            repo.eliminarTarea(tarea)
        }
    }

    // Variables para coordenadas temporales
    private val _tempLat = MutableStateFlow<Double?>(null)
    private val _tempLon = MutableStateFlow<Double?>(null)

    val tempLat = _tempLat.asStateFlow()
    val tempLon = _tempLon.asStateFlow()

    fun setTempUbicacion(lat: Double, lon: Double) {
        _tempLat.value = lat
        _tempLon.value = lon
    }

    fun limpiarTempUbicacion() {
        _tempLat.value = null
        _tempLon.value = null
    }

    // Variables para formulario temporal (título y descripción)
    private val _tempTitulo = MutableStateFlow("")
    private val _tempDescripcion = MutableStateFlow("")

    val tempTitulo = _tempTitulo.asStateFlow()
    val tempDescripcion = _tempDescripcion.asStateFlow()

    // Guardo temporalmente los valores del formulario que estoy editando
    fun setTempFormulario(titulo: String, descripcion: String) {
        _tempTitulo.value = titulo
        _tempDescripcion.value = descripcion
    }

    fun limpiarTempFormulario() {
        _tempTitulo.value = ""
        _tempDescripcion.value = ""
    }
}