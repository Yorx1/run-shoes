package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var registrarNuevoTextView: TextView

    private lateinit var auth: FirebaseAuth
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Buscar vistas
        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)
        registrarNuevoTextView = findViewById(R.id.registrarNuevo)

        // Acciones de clic
        loginButton.setOnClickListener { iniciarSesion() }
        forgotPasswordTextView.setOnClickListener { navegarA(RestpassActivity::class.java) }
        registrarNuevoTextView.setOnClickListener { navegarA(RegistroActivity::class.java) }
    }

    private fun iniciarSesion() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validación de campos
        if (email.isEmpty()) {
            emailEditText.error = "El correo es obligatorio"
            return
        }
        if (password.isEmpty()) {
            passwordEditText.error = "La contraseña es obligatoria"
            return
        }

        // Iniciar sesión con Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    obtenerYRedirigirSegunRol(email)
                } else {
                    Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun obtenerYRedirigirSegunRol(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val usuario = firestoreHelper.getUserByEmail(email)
                val rol = firestoreHelper.getUserRole(email)

                withContext(Dispatchers.Main) {
                    if (usuario != null && rol != null) {
                        // Guardar sesión del usuario
                        guardarDatosSesion(usuario)

                        // Redirigir según el rol
                        when (rol) {
                            "cliente" -> navegarA(MainActivity::class.java)
                            "vendedor" -> navegarA(AdminMainActivity::class.java)
                            else -> Toast.makeText(this@LoginActivity, "Rol no reconocido", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "No se pudo obtener el usuario o el rol",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error al obtener el rol: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun guardarDatosSesion(usuario: Usuario) {
        UserSession.apply {
            email = usuario.email
            nombres = usuario.nombres
            apellidos = usuario.apellidos
        }
    }

    private fun <T> navegarA(activity: Class<T>) {
        val intent = Intent(this, activity)
        startActivity(intent)
        finish()
    }
}
