package com.example.proyectofinalam

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Este método se llamará cuando recibas una notificación en primer plano
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Aquí puedes manejar la notificación, por ejemplo, mostrarla al usuario
        if (remoteMessage.data.isNotEmpty()) {
            // Aquí puedes extraer datos adicionales si los envías desde tu servidor
            val message = remoteMessage.data["message"]
            // Lógica para mostrar la notificación al usuario
            sendNotification(message)
        }

        // Si la notificación tiene un mensaje en el campo de "notification", muestra una notificación en pantalla
        remoteMessage.notification?.let {
            sendNotification(it.body)
        }
    }

    // Método para enviar la notificación a la interfaz de usuario
    private fun sendNotification(messageBody: String?) {
        // Obtener el NotificationManager del sistema
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear un canal de notificación para Android 8.0 (API 26) y superior
        val notificationChannelId = "fcm_default_channel"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "FCM Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Crear la notificación
        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Notificación FCM")
            .setContentText(messageBody)
            .setSmallIcon(R.mipmap.ic_launcher) // o usa otro ícono disponible
            .build()

        // Mostrar la notificación
        notificationManager.notify(0, notification)
    }
}
