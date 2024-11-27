package com.example.proyectofinalam

object CartManager {
    val cartItems = mutableListOf<Producto>()

    fun addItem(producto: Producto) {
        cartItems.add(producto)
    }

    fun actualizarCantidad(producto: Producto, nuevaCantidad: Int) {
        val item = cartItems.find { it.id == producto.id }
        item?.cantidad = nuevaCantidad
    }

    fun eliminarProducto(producto: Producto) {
        cartItems.removeIf { it.id == producto.id }
    }

}
