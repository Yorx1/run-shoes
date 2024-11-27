package com.example.proyectofinalam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalam.R
import com.example.proyectofinalam.FavoriteItem
import com.bumptech.glide.Glide // Asegúrate de tener Glide o cualquier otra biblioteca para cargar imágenes

class FavoriteAdapter(private val favoritesList: List<FavoriteItem>) : RecyclerView.Adapter<FavoriteAdapter.FavoritesViewHolder>() {

    class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_item, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val favoriteItem = favoritesList[position]
        holder.productName.text = favoriteItem.name
        holder.productPrice.text = "$${favoriteItem.price}"
        // Cargar la imagen usando Glide o cualquier otra biblioteca
        Glide.with(holder.productImage.context).load(favoriteItem.imageUrl).into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return favoritesList.size
    }
}
