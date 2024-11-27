package com.example.proyectofinalam

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.Query

data class Usuario(
    val id: Int = 0,
    val nombres: String = "",
    val apellidos: String = "",
    val email: String = ""
)

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val tokensCollection = db.collection("tokens")
    private val productsCollection = db.collection("products")

    companion object {
        const val TAG = "FirestoreHelper"
    }

    // Insertar un nuevo usuario
    suspend fun insertUser(nombres: String, apellidos: String, password: String, email: String): Boolean {
        val user = hashMapOf(
            "nombres" to nombres,
            "apellidos" to apellidos,
            "password" to password,
            "email" to email,
            "rol" to "cliente" // Rol predeterminado para los nuevos usuarios
        )

        return try {
            usersCollection.document(email).set(user).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al insertar usuario: ${e.message}", e)
            false
        }
    }

    // Verificar si el usuario existe por correo y contraseña
    suspend fun checkUser(email: String, password: String): Boolean {
        return try {
            val document = usersCollection.document(email).get().await()
            if (document.exists()) {
                val storedPassword = document.getString("password")
                storedPassword == password
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al verificar usuario: ${e.message}", e)
            false
        }
    }

    // Obtener usuario por email
    suspend fun getUserByEmail(email: String): Usuario? {
        return try {
            val querySnapshot = usersCollection
                .whereEqualTo("email", email)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            if (document != null && document.exists()) {
                val id = document.id.hashCode()
                val nombres = document.getString("nombres") ?: ""
                val apellidos = document.getString("apellidos") ?: ""
                val emailDb = document.getString("email") ?: ""
                Usuario(id, nombres, apellidos, emailDb)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener usuario: ${e.message}", e)
            null
        }
    }

    // Actualizar la contraseña de un usuario
    suspend fun updatePassword(email: String, newPassword: String): Boolean {
        return try {
            usersCollection.document(email).update("password", newPassword).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar contraseña: ${e.message}", e)
            false
        }
    }

    // Verificar si un correo ya está registrado
    suspend fun isEmailRegistered(email: String): Boolean {
        return try {
            val querySnapshot = usersCollection
                .whereEqualTo("email", email)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e(TAG, "Error al verificar correo registrado: ${e.message}", e)
            false
        }
    }

    // Guardar un token asociado a un usuario
    suspend fun saveTokenForUser(email: String, token: String) {
        val fechaCreacion = System.currentTimeMillis()

        val tokenData = hashMapOf(
            "email" to email,
            "token" to token,
            "fecha_creacion" to fechaCreacion
        )

        try {
            tokensCollection.document(email).set(tokenData).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar token: ${e.message}", e)
        }
    }

    // Obtener el rol de un usuario
    suspend fun getUserRole(email: String): String? {
        return try {
            val querySnapshot = usersCollection
                .whereEqualTo("email", email)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
            if (document != null && document.exists()) {
                document.getString("rol")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener rol del usuario: ${e.message}", e)
            null
        }
    }

    // Guardar un nuevo producto
    suspend fun saveProduct(productData: Map<String, Any>): Boolean {
        return try {
            productsCollection.add(productData).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar producto: ${e.message}", e)
            false
        }
    }

    // Obtener lista de productos favoritos
    suspend fun getFavoriteItems(): List<FavoriteItem> {
        return try {
            val snapshot = productsCollection.get().await()
            snapshot.documents.map { doc ->
                FavoriteItem(
                    id = doc.id,
                    name = doc.getString("nombre") ?: "",
                    price = doc.getDouble("precio") ?: 0.0,
                    imageUrl = doc.getString("imagenUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener productos favoritos: ${e.message}", e)
            emptyList()
        }
    }

    private val pagosCollection = db.collection("pagos")

    // Guardar un pago
    suspend fun savePago(pago: Pago): Boolean {
        return try {
            db.collection("pagos").add(pago).await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreHelper", "Error al guardar el pago", e)
            false
        }
    }

    fun obtenerSiguienteOrden(callback: (String?) -> Unit) {
        db.collection("pagos")
            .orderBy("orden", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                val lastOrder = documents.firstOrNull()
                val orderNumber = if (lastOrder != null) {
                    val orderStr = lastOrder.getString("orden")?.removePrefix("ordendecompra")?.toInt() ?: 0
                    orderStr + 1
                } else {
                    1
                }
                val orderId = "ordendecompra%02d".format(orderNumber)
                callback(orderId)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null) // Manejo de errores
            }

    }

}
