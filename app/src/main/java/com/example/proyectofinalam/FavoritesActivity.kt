package com.example.proyectofinalam

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.proyectofinalam.FavoriteItem
import com.example.proyectofinalam.FavoriteAdapter

class FavoritesActivity : AppCompatActivity() {

    private lateinit var favoritesRecyclerView: RecyclerView
    private lateinit var messageTextView: TextView
    private val firestore = FirebaseFirestore.getInstance()
    private val favoritesList = mutableListOf<FavoriteItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView)
        messageTextView = findViewById(R.id.messageTextView)

        // Configurar el RecyclerView
        favoritesRecyclerView.layoutManager = LinearLayoutManager(this)

        // Cargar favoritos desde Firestore
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        // Suponiendo que tus favoritos están en una colección llamada "favorites"
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail != null) {
            firestore.collection("favorites")
                .document(userEmail)
                .collection("userFavorites") // Cambia según tu estructura
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        mostrarMensajeNoFavoritos()
                    } else {
                        for (document in documents) {
                            // Aquí convierte el documento a tu objeto favorito
                            val favoriteItem = document.toObject(FavoriteItem::class.java)
                            favoritesList.add(favoriteItem)
                        }
                        // Aquí actualiza el adapter del RecyclerView
                        favoritesRecyclerView.adapter = FavoriteAdapter(favoritesList)
                        messageTextView.visibility = TextView.GONE // Oculta el mensaje si hay favoritos
                    }
                }
                .addOnFailureListener { exception ->
                    // Manejo de errores
                    mostrarMensajeNoFavoritos()
                }
        } else {
            mostrarMensajeNoFavoritos()
        }
    }

    private fun mostrarMensajeNoFavoritos() {
        favoritesRecyclerView.visibility = RecyclerView.GONE // Oculta el RecyclerView
        messageTextView.visibility = TextView.VISIBLE // Muestra el mensaje
    }
}
