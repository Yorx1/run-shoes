package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProductoDetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto_detalle)

        val producto = intent.getSerializableExtra("PRODUCTO") as? Producto

        if (producto != null) {
            // Actualiza la interfaz con los datos del producto
            findViewById<TextView>(R.id.tv_product_name).text = producto.nombre
            findViewById<TextView>(R.id.tv_product_price).text = "S/. ${producto.precio}"
            findViewById<TextView>(R.id.tv_product_description).text = producto.descripcion

            val productImage = findViewById<ImageView>(R.id.iv_product_image)

            Glide.with(this)
                .load(producto.imagenUrl)
                .placeholder(R.drawable.baseline_placeholder_image)
                .error(R.drawable.baseline_error)
                .into(productImage)

            val addToCartButton = findViewById<Button>(R.id.btn_add_to_cart)

            addToCartButton.setOnClickListener {
                CartManager.addItem(producto)
                Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
            }

        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    // Redirige a la actividad Home
                    startActivity(Intent(this@ProductoDetalleActivity, MainActivity::class.java))
                    true
                }
                R.id.navSearch -> {
                    // Redirige a la actividad Search
                    startActivity(Intent(this@ProductoDetalleActivity, BusquedaActivity::class.java))
                    true
                }
                R.id.navCarrito -> {
                    // Redirige a la actividad Cart
                    startActivity(Intent(this@ProductoDetalleActivity, CartActivity::class.java))
                    true
                }
                R.id.navUser -> {
                    // Lógica para cerrar sesión (opcional)
                    startActivity(Intent(this@ProductoDetalleActivity, UsuarioActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

    }
}


