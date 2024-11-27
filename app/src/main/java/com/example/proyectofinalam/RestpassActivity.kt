package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RestpassActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var enviarButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestoreHelper: FirestoreHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restpass)

        // Inicializamos las vistas y FirebaseAuth
        emailEditText = findViewById(R.id.et_email)
        enviarButton = findViewById(R.id.btn_confcorreo)
        auth = FirebaseAuth.getInstance() // FirebaseAuth para el envío de correo
        firestoreHelper = FirestoreHelper() // FirestoreHelper para validar emails en Firestore

        enviarButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            // Validar que el campo de email no esté vacío
            if (TextUtils.isEmpty(email)) {
                emailEditText.error = "Se requiere el correo"
                return@setOnClickListener
            }

            // Verifica si el correo está registrado en Firestore
            lifecycleScope.launch {
                val isRegistered = firestoreHelper.isEmailRegistered(email)

                if (isRegistered) {
                    // Si está registrado, envía el correo de recuperación con FirebaseAuth
                    enviarCorreoRecuperacionFirebase(email)
                } else {
                    // Si no está registrado en Firestore, muestra un mensaje de error
                    Toast.makeText(this@RestpassActivity, "Este correo no está registrado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Función para enviar el correo de recuperación usando Firebase Authentication
    private fun enviarCorreoRecuperacionFirebase(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@RestpassActivity, "Correo enviado!", Toast.LENGTH_SHORT).show()
                    // Redirige al LoginActivity después de enviar el correo
                    val intent = Intent(this@RestpassActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Manejo de errores si no se puede enviar el correo
                    Toast.makeText(this@RestpassActivity, "No se pudo enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
