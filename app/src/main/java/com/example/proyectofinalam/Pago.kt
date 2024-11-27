package com.example.proyectofinalam

data class Pago(
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val direccion: String,
    val telefono: String,
    val tipoDocumento: String,
    val tipoFactura: String,
    val documento: String,
    val montoTotal: Double,
    val orden: String? = null, // Campo para el número de orden
    val timestamp: Long = System.currentTimeMillis()
)
