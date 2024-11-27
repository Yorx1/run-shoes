package com.example.proyectofinalam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalam.R

class SizeAdapter(
    private val sizes: MutableList<Size>
) : RecyclerView.Adapter<SizeAdapter.SizeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_size, parent, false)
        return SizeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        val size = sizes[position]
        holder.bind(size)
    }

    override fun getItemCount(): Int = sizes.size

    inner class SizeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val sizeEditText: EditText = view.findViewById(R.id.sizeEditText)
        private val quantityEditText: EditText = view.findViewById(R.id.quantityEditText)

        fun bind(size: Size) {
            sizeEditText.setText(size.size)
            quantityEditText.setText(size.quantity.toString())

            sizeEditText.setOnFocusChangeListener { _, _ ->
                size.size = sizeEditText.text.toString()
            }

            quantityEditText.setOnFocusChangeListener { _, _ ->
                size.quantity = quantityEditText.text.toString().toIntOrNull() ?: 0
            }
        }
    }
}
