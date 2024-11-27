package com.example.proyectofinalam
import java.io.Serializable

data class Producto(
    val id: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    val precio: Double = 0.0,
    var cantidad: Int = 0,
    var imagenUrl: String = "",
    var tallas: List<String> = listOf()  // Ahora es una lista de tallas
) : Serializable {
    // Constructor vacío requerido por Firebase
    constructor() : this("", "", "", 0.0, 0, "", listOf())
}
