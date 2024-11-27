package com.example.proyectofinalam

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductoAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.producto_item, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]

        // Asignar el nombre del producto
        holder.productName.text = producto.nombre

        // Asignar el precio del producto
        holder.productPrice.text = " S/. ${producto.precio}"

        // Cargar la imagen del producto usando Glide
        Glide.with(holder.productImage.context)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.baseline_placeholder_image) // Imagen predeterminada mientras se carga
            .error(R.drawable.baseline_error)                  // Imagen predeterminada si falla la carga
            .into(holder.productImage)

        // Configurar el evento de clic
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductoDetalleActivity::class.java)

            // Pasar el objeto Producto al intent
            intent.putExtra("PRODUCTO", producto) // Producto debe ser Serializable o Parcelable
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    // ViewHolder que contiene las vistas de cada item
    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productImage: ImageView = itemView.findViewById(R.id.product_image1)
    }
}