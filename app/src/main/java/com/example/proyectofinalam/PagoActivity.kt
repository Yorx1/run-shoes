package com.example.proyectofinalam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class PagoActivity : AppCompatActivity() {

    private lateinit var editNombres: EditText
    private lateinit var editApellidos: EditText
    private lateinit var editCorreo: EditText
    private lateinit var editDireccion: EditText
    private lateinit var editPhone: EditText
    private lateinit var spinnerDocumentType: Spinner
    private lateinit var spinnerInvoiceType: Spinner
    private lateinit var editDocument: EditText
    private lateinit var labelBienvenida: TextView
    private lateinit var labelTotal: TextView
    private lateinit var labelTotalProductos: TextView
    private val firestoreHelper = FirestoreHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago)

        // Enlazar vistas con la interfaz
        editNombres = findViewById(R.id.edit_nombres)
        editApellidos = findViewById(R.id.edit_apellidos)
        editCorreo = findViewById(R.id.edit_correo)
        editDireccion = findViewById(R.id.edit_direccion)
        editPhone = findViewById(R.id.edit_phone)
        spinnerDocumentType = findViewById(R.id.spinner_document_type)
        spinnerInvoiceType = findViewById(R.id.spinner_invoice_type)
        editDocument = findViewById(R.id.edit_document)
        labelBienvenida = findViewById(R.id.label_bienvenida)
        labelTotal = findViewById(R.id.total_amount)
        labelTotalProductos = findViewById(R.id.total_products)

        // Botón de confirmación
        val confirmButton = findViewById<Button>(R.id.btn_comprar)
        confirmButton.setOnClickListener {
            if (editNombres.text.isNullOrEmpty() || editApellidos.text.isNullOrEmpty() || editCorreo.text.isNullOrEmpty()) {
                Log.e("PagoActivity", "Por favor completa todos los campos obligatorios.")
                Toast.makeText(this, "Por favor completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            } else {
                guardarPagoEnFirestore(
                    intent.getDoubleExtra("totalAmount", 0.0),
                    intent.getIntExtra("totalProductos", 0)
                )
            }
        }

        // Obtener los datos del Intent
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        val totalProductos = intent.getIntExtra("totalProductos", 0)

        // Mostrar datos en la pantalla
        labelTotal.text = "Total: S/. $totalAmount"
        labelTotalProductos.text = "($totalProductos productos)"

        // Personalizar el saludo
        mostrarNombreUsuario()
    }

    private fun mostrarNombreUsuario() {
        val nombreUsuario = UserSession.nombres ?: "Usuario"
        labelBienvenida.text = "Hola, $nombreUsuario!"
    }

    private fun guardarPagoEnFirestore(totalAmount: Double, totalProductos: Int) {
        // Obtener datos de la interfaz
        val nombres = editNombres.text.toString()
        val apellidos = editApellidos.text.toString()
        val correo = editCorreo.text.toString()
        val direccion = editDireccion.text.toString()
        val telefono = editPhone.text.toString()
        val tipoDocumento = spinnerDocumentType.selectedItem.toString()
        val tipoFactura = spinnerInvoiceType.selectedItem.toString()
        val documento = editDocument.text.toString()

        // Llamar a la función para obtener el siguiente número de orden
        firestoreHelper.obtenerSiguienteOrden { orderId ->
            if (orderId != null) {
                // Crear un objeto de tipo Pago con el número de orden generado
                val pago = Pago(
                    nombres = nombres,
                    apellidos = apellidos,
                    correo = correo,
                    direccion = direccion,
                    telefono = telefono,
                    tipoDocumento = tipoDocumento,
                    tipoFactura = tipoFactura,
                    documento = documento,
                    montoTotal = totalAmount,
                    orden = orderId
                )

                // Guardar en Firestore
                lifecycleScope.launch {
                    val result = firestoreHelper.savePago(pago)
                    if (result) {
                        Log.d("PagoActivity", "Datos de compra guardados exitosamente.")
                        Toast.makeText(this@PagoActivity, "Pago registrado con éxito", Toast.LENGTH_SHORT).show()

                        // Redirigir a QrpagoActivity
                        val intent = Intent(this@PagoActivity, QrpagoActivity::class.java)
                        intent.putExtra("NOMBRES", nombres)
                        intent.putExtra("APELLIDOS", apellidos)
                        intent.putExtra("CORREO", correo)
                        intent.putExtra("DIRECCION", direccion)
                        intent.putExtra("TELEFONO", telefono)
                        intent.putExtra("TIPO_DOCUMENTO", tipoDocumento)
                        intent.putExtra("TIPO_FACTURA", tipoFactura)
                        intent.putExtra("DOCUMENTO", documento)
                        intent.putExtra("MONTO_TOTAL", totalAmount)
                        intent.putExtra("ORDEN", orderId)
                        intent.putExtra("totalProductos", totalProductos) // Pasar el número de productos
                        startActivity(intent)
                    } else {
                        Log.e("PagoActivity", "Error al guardar datos de compra.")
                        Toast.makeText(this@PagoActivity, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Manejo de errores si no se pudo generar el número de orden
                Toast.makeText(this@PagoActivity, "Error al generar el número de orden.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
