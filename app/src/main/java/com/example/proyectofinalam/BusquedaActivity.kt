package com.example.proyectofinalam

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class BusquedaActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var searchButton: ImageButton
    private lateinit var busquedaAdapter: BusquedaAdapter
    private val filteredList: MutableList<Producto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busqueda)

        // Inicializar Firestore
        val db = FirebaseFirestore.getInstance()

        // Inicializar vistas
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)

        // Configurar RecyclerView
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        busquedaAdapter = BusquedaAdapter(filteredList)
        searchResultsRecyclerView.adapter = busquedaAdapter

        // Configurar listener para el botón de búsqueda
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                performSearch(query, db)
            } else {
                Toast.makeText(this, "Por favor ingresa un término de búsqueda", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performSearch(query: String, db: FirebaseFirestore) {
        // Convertir la consulta a minúsculas
        val lowerCaseQuery = query.toLowerCase()

        // Realizar la consulta en Firestore. Buscaremos los productos por el nombre (en minúsculas).
        db.collection("Products")
            .get()
            .addOnSuccessListener { documents ->
                filteredList.clear()

                // Filtrar los productos basados en el nombre (sin importar mayúsculas o minúsculas)
                for (document in documents) {
                    val product = document.toObject(Producto::class.java)
                    val name = product.nombre?.toLowerCase()

                    // Comparar el nombre del producto con la búsqueda (también en minúsculas)
                    if (name != null && name.contains(lowerCaseQuery)) {
                        filteredList.add(product)
                    }
                }

                if (filteredList.isEmpty()) {
                    Toast.makeText(this, "No se encontraron productos con ese nombre", Toast.LENGTH_SHORT).show()
                } else {
                    busquedaAdapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al buscar productos: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
