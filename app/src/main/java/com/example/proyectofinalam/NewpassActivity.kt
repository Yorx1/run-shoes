package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class NewpassActivity : AppCompatActivity() {

    private lateinit var nuevaPasswordEditText: EditText
    private lateinit var confirmarPasswordEditText: EditText
    private lateinit var restablecerButton: Button
    lateinit var firestoreHelper: FirestoreHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newpass)

        nuevaPasswordEditText = findViewById(R.id.et_nuevacontraseña)
        confirmarPasswordEditText = findViewById(R.id.et_confcontraseña)
        restablecerButton = findViewById(R.id.btn_restcontraseña)
        firestoreHelper = FirestoreHelper()

        restablecerButton.setOnClickListener {
            val nuevaPassword = nuevaPasswordEditText.text.toString().trim()
            val confirmarPassword = confirmarPasswordEditText.text.toString().trim()

            if (TextUtils.isEmpty(nuevaPassword) || TextUtils.isEmpty(confirmarPassword)) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nuevaPassword != confirmarPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ejecutar la actualización de la contraseña dentro de una corrutina
            lifecycleScope.launch {
                val email = UserSession.email // Aquí podría ser nullable

                email?.let {
                    val success = firestoreHelper.updatePassword(it, nuevaPassword)
                    if (success) {
                        Toast.makeText(this@NewpassActivity, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        // Redirigir al login si es necesario
                        val intent = Intent(this@NewpassActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@NewpassActivity, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this@NewpassActivity, "No se pudo obtener el email del usuario", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
