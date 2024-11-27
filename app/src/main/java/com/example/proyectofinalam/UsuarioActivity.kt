package com.example.proyectofinalam

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuarioActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        // Inicializar los TextViews
        userNameTextView = findViewById(R.id.userName)
        userEmailTextView = findViewById(R.id.userEmail)

        // Mostrar la información del usuario
        mostrarInformacionUsuario()
    }

    private fun mostrarInformacionUsuario() {
        // Obtener el correo electrónico del usuario autenticado
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail != null) {
            // Cargar los datos del usuario desde Firestore
            cargarDatosUsuario(userEmail)
        } else {
            // En caso de que no haya un usuario autenticado
            userNameTextView.text = "Usuario no autenticado"
            userEmailTextView.text = "Correo no disponible"
        }
    }

    private fun cargarDatosUsuario(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val usuario = firestoreHelper.getUserByEmail(email)
            withContext(Dispatchers.Main) {
                if (usuario != null) {
                    // Mostrar el nombre completo y el email
                    userNameTextView.text = "${usuario.nombres} ${usuario.apellidos}"
                    userEmailTextView.text = usuario.email
                } else {
                    // En caso de que el usuario no exista en Firestore
                    userNameTextView.text = "Nombre no disponible"
                    userEmailTextView.text = "Email no disponible"
                }
            }
        }
    }
}
