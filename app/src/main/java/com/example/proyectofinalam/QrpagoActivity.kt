package com.example.proyectofinalam

import android.os.Build
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.app.PendingIntent
import android.provider.MediaStore
import android.content.Context
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.io.ByteArrayOutputStream
import java.util.*
import android.app.NotificationManager
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart

class QrpagoActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var uploadImageButton: Button
    private lateinit var previewImageView: ImageView
    private lateinit var payButton: Button
    private var selectedImageUri: Uri? = null

    private val db = FirebaseFirestore.getInstance() // Referencia a Firestore
    private val auth = FirebaseAuth.getInstance() // Para obtener el usuario si es necesario

    companion object {
        const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrpago)

        // Crear el canal de notificación para Android 8.0 (API 26) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "payment_notification_channel"
            val channelName = "Payment Notifications"
            val channelDescription = "Notifications related to payment status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Registrar el canal con el NotificationManager
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Referenciar elementos del layout
        backButton = findViewById(R.id.backButton)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        previewImageView = findViewById(R.id.previewImageView)
        payButton = findViewById(R.id.payButton)

        // Acción para el botón de regresar
        backButton.setOnClickListener {
            finish() // Cierra la actividad actual
        }

        // Acción para el botón de subir imagen
        uploadImageButton.setOnClickListener {
            openImageChooser()
        }

        // Acción para el botón de pago
        payButton.setOnClickListener {
            // Aquí puedes agregar la lógica para guardar el pago y luego enviar el correo
            enviarCorreo()
        }
    }

    // Método para abrir el selector de imágenes
    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Método para manejar el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            try {
                // Muestra la imagen seleccionada en la vista previa
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                previewImageView.setImageBitmap(bitmap)
                previewImageView.visibility = ImageView.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarCorreo() {
        // Obtener los datos del Intent
        val nombres = intent.getStringExtra("NOMBRES")
        val apellidos = intent.getStringExtra("APELLIDOS")
        val correo = intent.getStringExtra("CORREO")
        val direccion = intent.getStringExtra("DIRECCION")
        val telefono = intent.getStringExtra("TELEFONO")
        val tipoDocumento = intent.getStringExtra("TIPO_DOCUMENTO")
        val tipoFactura = intent.getStringExtra("TIPO_FACTURA")
        val documento = intent.getStringExtra("DOCUMENTO")
        val totalAmount = intent.getStringExtra("MONTO_TOTAL")
        val orderId = intent.getStringExtra("ORDEN")

        val destinatario = "smatiasgm11@gmail.com" // Correo de destino
        val remitente = "pollo30080@gmail.com" // Correo de origen (debe ser una cuenta Gmail)
        val contrasena = "gcbg kuun vsht holh" // Usar contraseña de aplicación (no la de la cuenta)

        // Configuración SMTP de Gmail
        val properties = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.socketFactory.port", "465")
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", "465")
        }

        val session = Session.getDefaultInstance(properties, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(remitente, contrasena) // Usar la cuenta y contraseña de aplicación
            }
        })

        try {
            // Crear el mensaje de correo
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(remitente))
                addRecipient(Message.RecipientType.TO, InternetAddress(destinatario))
                subject = "Detalles de pago"
            }

            // Crear el cuerpo del mensaje (Texto HTML)
            val bodyPart = MimeBodyPart().apply {
                setContent(
                    """
                <html>
                <body>
                    <h2>Detalles de pago</h2>
                    <p><b>Orden de pago:</b> $orderId</p>
                    <p><b>Nombres:</b> $nombres</p>
                    <p><b>Apellidos:</b> $apellidos</p>
                    <p><b>Correo:</b> $correo</p>
                    <p><b>Dirección:</b> $direccion</p>
                    <p><b>Teléfono:</b> $telefono</p>
                    <p><b>Tipo de Factura:</b> $tipoFactura</p>
                    <p><b>Tipo de Documento:</b> $tipoDocumento</p>
                    <p><b>Documento:</b> $documento</p>
                    <p><b>Monto Total:</b> $totalAmount</p>
                </body>
                </html>
                """, "text/html"
                )
            }

            // Crear MimeMultipart
            val multipart = MimeMultipart()

            // Agregar el cuerpo del mensaje
            multipart.addBodyPart(bodyPart)

            // Adjuntar la imagen
            val file = convertUriToFile(selectedImageUri!!, this@QrpagoActivity) // Obtener el archivo de la URI
            if (file != null) {
                val attachmentPart = MimeBodyPart()
                attachmentPart.attachFile(file)
                multipart.addBodyPart(attachmentPart)  // Adjuntar la imagen
            }

            // Establecer el contenido de la imagen
            message.setContent(multipart)

            // Hilo para enviar el correo
            Thread {
                try {
                    Transport.send(message)
                    runOnUiThread {
                        Toast.makeText(this@QrpagoActivity, "Correo enviado exitosamente", Toast.LENGTH_SHORT).show()

                        // Crear y mostrar la notificación después de enviar el correo
                        showNotification("Correo enviado", "El correo de detalles de pago ha sido enviado exitosamente.")
                    }
                } catch (e: Exception) {
                    Log.e("EnviarCorreo", "Error al enviar correo: ${e.message}")
                    runOnUiThread {
                        Toast.makeText(this@QrpagoActivity, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()

        } catch (e: Exception) {
            Log.e("EnviarCorreo", "Error de configuración: ${e.message}")
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "payment_notification_channel"

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification) // Usa un icono adecuado
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss al tocarla
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }


    // Convertir la URI en un archivo de imagen
    fun convertUriToFile(uri: Uri, context: Context): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        if (inputStream != null) {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val file = File(context.cacheDir, "image.jpg") // Puedes cambiar el nombre y formato
            val outputStream: OutputStream = FileOutputStream(file)

            // Comprimir la imagen (opcional)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            inputStream.close()
            outputStream.close()

            return file
        }
        return null
    }
}
