package com.example.proyectofinalam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalam.R

class BusquedaAdapter(private val productList: List<Producto>) : RecyclerView.Adapter<BusquedaAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.producto_item, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productList[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int = productList.size

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.product_image1)
        private val productName: TextView = itemView.findViewById(R.id.product_name)
        private val productPrice: TextView = itemView.findViewById(R.id.product_price)

        fun bind(producto: Producto) {
            productName.text = producto.nombre
            productPrice.text = "S/${producto.precio}" // Mostrar el precio con formato
            Glide.with(itemView.context)
                .load(producto.imagenUrl)
                .placeholder(R.drawable.baseline_placeholder_image) // Opcional: Imagen de carga
                .error(R.drawable.baseline_error)           // Opcional: Imagen de error
                .into(productImage)
        }
    }
}
