import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalam.Producto
import com.example.proyectofinalam.R
import com.example.proyectofinalam.CartManager

class CartAdapter(
    private val cartItems: MutableList<Producto>,
    private val onCartUpdated: () -> Unit // Callback para actualizar el carrito
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        val increaseQuantityButton: ImageButton =
            itemView.findViewById(R.id.increaseQuantityButton)
        val decreaseQuantityButton: ImageButton =
            itemView.findViewById(R.id.decreaseQuantityButton)
        val removeFromCartButton: ImageButton =
            itemView.findViewById(R.id.removeFromCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val producto = cartItems[position]
        holder.productName.text = producto.nombre
        holder.productPrice.text = "S/. ${producto.precio}"
        holder.productQuantity.text = producto.cantidad.toString() // Mostrar cantidad del modelo
        Glide.with(holder.itemView.context)
            .load(producto.imagenUrl)
            .placeholder(R.drawable.baseline_placeholder_image)
            .error(R.drawable.baseline_error)
            .into(holder.productImage)

        // Lógica para aumentar cantidad
        holder.increaseQuantityButton.setOnClickListener {
            producto.cantidad++ // Incrementar cantidad en el modelo
            holder.productQuantity.text = producto.cantidad.toString()
            onCartUpdated() // Actualizar el total
        }

        // Lógica para disminuir cantidad
        holder.decreaseQuantityButton.setOnClickListener {
            if (producto.cantidad > 1) {
                producto.cantidad-- // Decrementar cantidad en el modelo
                holder.productQuantity.text = producto.cantidad.toString()
                onCartUpdated() // Actualizar el total
            }
        }

        // Lógica para eliminar del carrito
        holder.removeFromCartButton.setOnClickListener {
            cartItems.removeAt(position) // Eliminar de la lista local
            CartManager.cartItems.remove(producto) // Eliminar también del CartManager
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartItems.size)
            onCartUpdated() // Actualizar el total
        }
    }

    override fun getItemCount(): Int = cartItems.size
}
