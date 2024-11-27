package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: TextView
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el TextView
        welcomeTextView = findViewById(R.id.welcomeTextView)

        // 1. Primero, encuentra el ViewPager2 y configura un Adapter
        val viewPager2 = findViewById<ViewPager2>(R.id.viewPager2)

        // Aquí se usa un ViewPager2 Adapter personalizado
        val adapter = CarouselAdapter() // Este es tu adapter personalizado que vas a crear
        viewPager2.adapter = adapter

        // 2. Configura el desplazamiento automático cada 2 segundos
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (viewPager2.currentItem + 1) % adapter.itemCount
                viewPager2.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // 3000ms = 3 segundos
            }
        }
        // Inicia el desplazamiento automático cuando se inicialice el ViewPager2
        handler.postDelayed(runnable, 3000) // Empieza después de 3 segundos

        // Llama a la función para mostrar el nombre del usuario
        mostrarNombreUsuario()

        // Encuentra los botones por su ID
        val userButton: ImageButton = findViewById(R.id.userButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val verTodoTextView: TextView = findViewById(R.id.verTodoTextView)
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButtonNav: ImageButton = findViewById(R.id.searchButtonNav)
        val favoritesButton: ImageButton = findViewById(R.id.favoritesButton)
        val cartButton: ImageButton = findViewById(R.id.cartButton)


        // Configura los OnClickListeners para cada botón
        userButton.setOnClickListener {
            val intent = Intent(this, UsuarioActivity::class.java)
            startActivity(intent)
        }

        searchButton.setOnClickListener {
            val intent = Intent(this, BusquedaActivity::class.java)
            startActivity(intent)
        }

        searchButtonNav.setOnClickListener {
            val intent = Intent(this, BusquedaActivity::class.java)
            startActivity(intent)
        }

        verTodoTextView.setOnClickListener {
            val intent = Intent(this, ProductoActivity::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        favoritesButton.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Favoritos clicked", Toast.LENGTH_SHORT).show() // Agregado para verificar
        }

        cartButton.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Carrito clicked", Toast.LENGTH_SHORT).show() // Agregado para verificar
        }
    }

    private fun mostrarNombreUsuario() {
        // Verifica si UserSession contiene datos y los usa para personalizar el texto
        val nombreUsuario = UserSession.nombres ?: "Usuario"
        welcomeTextView.text = "Bienvenido, $nombreUsuario"
    }
}
