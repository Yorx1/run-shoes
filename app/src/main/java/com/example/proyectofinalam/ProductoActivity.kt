package com.example.proyectofinalam

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ProductoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productoAdapter: ProductoAdapter
    private val productoList = mutableListOf<Producto>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_producto)

        // Configura el RecyclerView con un GridLayoutManager de 2 columnas
        recyclerView = findViewById(R.id.product_recycler_view)
        recyclerView.layoutManager = GridLayoutManager(this, 2)  // Esto es correcto en Kotlin

        // Inicializa el adaptador
        productoAdapter = ProductoAdapter(productoList)
        recyclerView.adapter = productoAdapter

        // Obtener los productos desde Firestore
        obtenerProductos()

        // Botón de retroceso
        findViewById<ImageButton>(R.id.buttonBack).setOnClickListener {
            finish()
        }

        // Configurar los listeners de los chips
        val chipNew = findViewById<com.google.android.material.chip.Chip>(R.id.chip_new)
        val chipPrice = findViewById<com.google.android.material.chip.Chip>(R.id.chip_price)
        val chipCategory = findViewById<com.google.android.material.chip.Chip>(R.id.chip_category)
        val chipPopularity = findViewById<com.google.android.material.chip.Chip>(R.id.chip_popularity)

        chipNew.setOnClickListener { filtrarProductosPor("Nuevos") }
        chipPrice.setOnClickListener { filtrarProductosPor("Precio") }
        chipCategory.setOnClickListener { filtrarProductosPor("Categoría") }
        chipPopularity.setOnClickListener { filtrarProductosPor("Popularidad") }

        // Cerrar el popup
        findViewById<RelativeLayout>(R.id.popup_container).visibility = View.GONE
        findViewById<androidx.appcompat.widget.AppCompatImageView>(R.id.close_popup).setOnClickListener {
            findViewById<RelativeLayout>(R.id.popup_container).visibility = View.GONE
        }
    }

    private fun obtenerProductos() {
        db.collection("products")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    productoList.clear()  // Limpia la lista antes de agregar nuevos productos
                    for (document in task.result!!) {
                        val producto = document.toObject(Producto::class.java)
                        productoList.add(producto)
                    }
                    productoAdapter.notifyDataSetChanged()  // Notifica al adaptador que la lista ha cambiado
                } else {
                    Log.d("ProductoActivity", "Error al obtener los productos: ", task.exception)
                }
            }
    }

    private fun filtrarProductosPor(criterio: String) {
        // Aquí puedes implementar la lógica de filtrado según el criterio
        Log.d("ProductoActivity", "Filtrando productos por: $criterio")
    }
}
