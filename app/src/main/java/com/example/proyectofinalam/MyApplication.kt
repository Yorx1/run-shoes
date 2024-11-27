package com.example.proyectofinalam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Crear el canal de notificaciones en segundo plano
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "payment_notification_channel"
            val channelName = "Payment Notifications"
            val channelDescription = "Notifications related to payment status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            // Crear el canal
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            // Obtener el NotificationManager y registrar el canal
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
