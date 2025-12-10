package com.example.proyectofinal.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// posibles estados para las tareas
enum class EstadoTarea(val estado: String) {
    PENDIENTE("Pendiente"),
    REALIZADA("Realizada"),
    CANCELADA("Cancelada")
}

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val fecha: Long,
    val latitud: Double? = null,
    val longitud: Double? = null,
    // Agrego el estado de la tarea, por defecto es PENDIENTE
    val estado: String = EstadoTarea.PENDIENTE.estado
)