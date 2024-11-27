package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referenciar las vistas
        val nombresInput = findViewById<EditText>(R.id.et_name)
        val apellidosInput = findViewById<EditText>(R.id.et_apellido)
        val emailInput = findViewById<EditText>(R.id.et_email)
        val passwordInput = findViewById<EditText>(R.id.et_password)
        val registerButton = findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val nombres = nombresInput.text.toString()
            val apellidos = apellidosInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // Validar campos
            if (nombres.isNotEmpty() && apellidos.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(nombres, apellidos, email, password)
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para registrar usuario en Firebase Authentication y Firestore
    private fun registerUser(nombres: String, apellidos: String, email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Registrar usuario en Firebase Authentication
                val authResult = withContext(Dispatchers.IO) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                }

                // Obtener el UID del usuario registrado
                val userId = authResult.user?.uid

                // Crear el objeto de usuario con los datos adicionales
                val userMap = hashMapOf(
                    "nombres" to nombres,
                    "apellidos" to apellidos,
                    "email" to email,
                    "userId" to userId,
                    "rol" to "cliente"
                )

                // Guardar los datos adicionales en Firestore
                if (userId != null) {
                    withContext(Dispatchers.IO) {
                        firestore.collection("users").document(userId).set(userMap).await()
                    }

                    Toast.makeText(this@RegistroActivity, "Usuario registrado", Toast.LENGTH_SHORT).show()

                    // Redirigir al LoginActivity después del registro exitoso
                    val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegistroActivity, "Error al obtener UID del usuario", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Manejar el error al registrar el usuario
                Toast.makeText(this@RegistroActivity, "Error al registrar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
