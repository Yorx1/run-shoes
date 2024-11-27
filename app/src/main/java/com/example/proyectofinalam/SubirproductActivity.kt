package com.example.proyectofinalam

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import com.example.proyectofinalam.Size
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubirproductActivity : AppCompatActivity() {
    private lateinit var productName: EditText
    private lateinit var productDescription: EditText
    private lateinit var productPrice: EditText
    private lateinit var productColors: EditText
    private lateinit var uploadImageButton: Button
    private lateinit var saveProductButton: Button
    private lateinit var productImageView: ImageView
    private lateinit var imagesRecyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var sizesRecyclerView: RecyclerView
    private lateinit var sizeAdapter: SizeAdapter
    private lateinit var addSizeButton: Button
    private var imageUrls: ArrayList<String> = ArrayList()
    private var sizes: MutableList<Size> = mutableListOf()
    private var imageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private lateinit var getImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subirproduct)

        // Inicializar los elementos de la interfaz
        productName = findViewById(R.id.productName)
        productDescription = findViewById(R.id.productDescription)
        productPrice = findViewById(R.id.productPrice)
        productColors = findViewById(R.id.productColor)
        uploadImageButton = findViewById(R.id.uploadImageButton)
        saveProductButton = findViewById(R.id.saveProductButton)
        productImageView = findViewById(R.id.productImageView)
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView)
        sizesRecyclerView = findViewById(R.id.sizesRecyclerView)
        addSizeButton = findViewById(R.id.addSizeButton)

        // Configurar RecyclerView de imágenes
        imageAdapter = ImageAdapter(imageUrls, this)
        imagesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        imagesRecyclerView.adapter = imageAdapter

        // Configurar RecyclerView de tallas
        sizeAdapter = SizeAdapter(sizes)
        sizesRecyclerView.layoutManager = LinearLayoutManager(this)
        sizesRecyclerView.adapter = sizeAdapter

        // Inicializar el lanzador de resultados
        getImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                imageUri = uri
                productImageView.setImageURI(imageUri)
                productImageView.visibility = View.VISIBLE
            }
        }

        uploadImageButton.setOnClickListener { selectImage() }
        saveProductButton.setOnClickListener { saveProduct() }
        addSizeButton.setOnClickListener { addNewSize() }
    }

    private fun addNewSize() {
        sizes.add(Size("", 0)) // Agrega una nueva talla vacía
        sizeAdapter.notifyItemInserted(sizes.size - 1)
    }

    private fun selectImage() {
        getImageLauncher.launch("image/*")
    }

    private fun saveProduct() {
        val name = productName.text.toString()
        val description = productDescription.text.toString()
        val price = productPrice.text.toString().toDoubleOrNull()
        val colors = productColors.text.toString().split(",").map { it.trim() }
        val enteredSizes = sizes.filter { it.size.isNotEmpty() && it.quantity > 0 }

        if (name.isNotEmpty() && description.isNotEmpty() && price != null && imageUri != null && enteredSizes.isNotEmpty()) {
            uploadImageAndSaveProduct(name, description, price, colors, enteredSizes)
        } else {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImageAndSaveProduct(name: String, description: String, price: Double, colors: List<String>, sizes: List<Size>) {
        val storageReference = storage.reference.child("products/${System.currentTimeMillis()}.jpg")
        val uploadTask = storageReference.putFile(imageUri!!)

        uploadTask.addOnSuccessListener {
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                val productData = hashMapOf(
                    "nombre" to name,
                    "descripcion" to description,
                    "precio" to price,
                    "colores" to colors,
                    "tallas" to sizes.map { "${it.size}:${it.quantity}" }, // Guardar tallas en formato texto
                    "imagenUrl" to uri.toString()
                )

                CoroutineScope(Dispatchers.IO).launch {
                    val success = FirestoreHelper().saveProduct(productData)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(this@SubirproductActivity, "Producto guardado exitosamente", Toast.LENGTH_SHORT).show()
                            finish() // Cerrar la actividad
                        } else {
                            Toast.makeText(this@SubirproductActivity, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
        }
    }
}
