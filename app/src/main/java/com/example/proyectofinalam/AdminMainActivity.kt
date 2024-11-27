package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminMainActivity : AppCompatActivity() {

    private lateinit var welcomeTextView: MaterialTextView
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainadmin)

        welcomeTextView = findViewById(R.id.welcomeTextView) // Solo asigna, no redeclares

        val adminSettingsButton = findViewById<ShapeableImageView>(R.id.adminsettingsBoton)
        val sloganTextView = findViewById<MaterialTextView>(R.id.sloganTextView)

        // Carga y muestra el nombre del administrador
        mostrarNombreAdmin()

        // Configura el evento de clic para el botón de configuración (admin settings)
        adminSettingsButton.setOnClickListener {
            Toast.makeText(this, "Configuración de administrador", Toast.LENGTH_SHORT).show()
        }

        // Configura los botones en el GridLayout
        val buttonInicio = findViewById<MaterialButton>(R.id.button_inicio)
        val buttonDashboard = findViewById<MaterialButton>(R.id.button_dashboard)
        val buttonNotificaciones = findViewById<MaterialButton>(R.id.button_notificaciones)
        val buttonIngresarProducto = findViewById<MaterialButton>(R.id.button_ingresar_producto)
        val buttonInventario = findViewById<MaterialButton>(R.id.button_inventario)
        val buttonOrdenes = findViewById<MaterialButton>(R.id.button_ordenes)
        val buttonReportes = findViewById<MaterialButton>(R.id.button_reportes)
        val buttonPromociones = findViewById<MaterialButton>(R.id.button_promociones)

        // Configura los eventos de clic para cada botón
        buttonInicio.setOnClickListener {
            Toast.makeText(this, "Inicio seleccionado", Toast.LENGTH_SHORT).show()
        }

        buttonDashboard.setOnClickListener {
            Toast.makeText(this, "Dashboard seleccionado", Toast.LENGTH_SHORT).show()
        }

        buttonNotificaciones.setOnClickListener {
            Toast.makeText(this, "Notificaciones seleccionadas", Toast.LENGTH_SHORT).show()
        }

        buttonIngresarProducto.setOnClickListener {
            val intent = Intent(this, SubirproductActivity::class.java)
            startActivity(intent)
        }

        buttonInventario.setOnClickListener {
            Toast.makeText(this, "Inventario seleccionado", Toast.LENGTH_SHORT).show()
        }

        buttonOrdenes.setOnClickListener {
            Toast.makeText(this, "Órdenes seleccionado", Toast.LENGTH_SHORT).show()
        }

        buttonReportes.setOnClickListener {
            Toast.makeText(this, "Reportes seleccionado", Toast.LENGTH_SHORT).show()
        }

        buttonPromociones.setOnClickListener {
            Toast.makeText(this, "Promociones seleccionado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarNombreAdmin() {
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (email != null) {
            cargarDatosUsuario(email)
        } else {
            welcomeTextView.text = "Bienvenido, Admin"
        }
    }

    private fun cargarDatosUsuario(email: String) {
        lifecycleScope.launch {
            val usuario = firestoreHelper.getUserByEmail(email)
            withContext(Dispatchers.Main) {
                if (usuario != null) {
                    welcomeTextView.text = "Bienvenido, ${usuario.nombres}"
                } else {
                    welcomeTextView.text = "Bienvenido, Admin"
                }
            }
        }
    }
}