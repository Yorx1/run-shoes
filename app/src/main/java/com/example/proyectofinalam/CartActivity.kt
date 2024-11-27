package com.example.proyectofinalam

import CartAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton


class CartActivity : AppCompatActivity() {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var messageTextView: TextView
    private lateinit var cartTotalLabel: TextView
    private lateinit var cartTotalAmount: TextView
    private lateinit var checkoutButton: MaterialButton
    private lateinit var cartTotalProducts: TextView  // Nuevo TextView para mostrar el número de productos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        messageTextView = findViewById(R.id.messageTextView)
        cartTotalLabel = findViewById(R.id.cartTotalLabel)
        cartTotalAmount = findViewById(R.id.cartTotalAmount)
        checkoutButton = findViewById(R.id.checkoutButton)
        cartTotalProducts = findViewById(R.id.cartTotalProducts)  // Inicializamos el nuevo TextView

        cargarCarrito()

        // Configura el botón "Proceder al Pago"
        checkoutButton.setOnClickListener {
            val total = CartManager.cartItems.sumOf { it.precio.toDouble() * it.cantidad }
            val totalProductos = CartManager.cartItems.sumOf { it.cantidad }

            // Crear el Intent para PagoActivity
            val intent = Intent(this, PagoActivity::class.java)
            intent.putExtra("totalAmount", total)  // Pasa el total al intent
            intent.putExtra("totalProductos", totalProductos)  // Pasa el número total de productos al intent
            startActivity(intent)  // Inicia la actividad solo al hacer clic en el botón
        }

    }

    private fun cargarCarrito() {
        if (CartManager.cartItems.isEmpty()) {
            mostrarMensajeNoCarrito()
        } else {
            configurarRecyclerView()
            calcularTotal()
        }
    }

    private fun mostrarMensajeNoCarrito() {
        cartRecyclerView.visibility = RecyclerView.GONE
        messageTextView.visibility = TextView.VISIBLE
        messageTextView.text = "El carrito está vacío."
    }

    private fun configurarRecyclerView() {
        cartRecyclerView.visibility = RecyclerView.VISIBLE
        messageTextView.visibility = TextView.GONE

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = CartAdapter(CartManager.cartItems.toMutableList()) {
            calcularTotal()
            actualizarCantidadProductos()
        }
    }

    private fun calcularTotal() {
        val total = CartManager.cartItems.sumOf { it.precio.toDouble() * it.cantidad }
        cartTotalAmount.text = "S/. $total"
    }

    private fun actualizarCantidadProductos() {
        val totalProductos = CartManager.cartItems.sumOf { it.cantidad }
        cartTotalProducts.text = "($totalProductos productos)"
    }
}
