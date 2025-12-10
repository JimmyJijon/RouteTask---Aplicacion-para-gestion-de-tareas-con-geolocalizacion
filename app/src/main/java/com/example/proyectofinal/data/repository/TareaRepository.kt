package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.dao.TareaDao
import com.example.proyectofinal.data.entity.Tarea

class TareaRepository(private val dao: TareaDao) {

    val todasLasTareas = dao.obtenerTareas()

    suspend fun agregarTarea(tarea: Tarea) {
        dao.insertar(tarea)
    }

    suspend fun actualizarTarea(tarea: Tarea) {
        dao.actualizar(tarea)
    }

    suspend fun eliminarTarea(tarea: Tarea) {
        dao.eliminar(tarea)
    }
}
