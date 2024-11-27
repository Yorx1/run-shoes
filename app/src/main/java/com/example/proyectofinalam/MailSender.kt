package com.example.proyectofinalam

import android.util.Log
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MailSender(private val email: String, private val password: String) {

    fun sendMail(to: String, emailSubject: String, messageBody: String): Boolean {
        val properties = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(email, password) // Usa la contraseña de la app aquí
            }
        })

        return try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(email))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                subject = emailSubject
                setText(messageBody)
            }

            Transport.send(message)
            true
        } catch (e: MessagingException) {
            // Aquí puedes loggear el error
            Log.e("MailSender", "Error al enviar correo: ${e.message}", e)
            false
        }
    }
}
