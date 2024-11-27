package com.example.proyectofinalam

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class CarouselAdapter : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    private val images = listOf(
        R.drawable.carrimg1, // Añade tus imágenes
        R.drawable.carrimg2,
        R.drawable.carrimg3,
        R.drawable.carrimg4
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val imageView = ImageView(parent.context)
        imageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Usamos FIT_CENTER para asegurarnos de que la imagen se ajuste sin ser recortada
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        return CarouselViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class CarouselViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
