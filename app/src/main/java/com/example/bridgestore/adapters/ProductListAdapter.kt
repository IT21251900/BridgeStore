package com.example.bridgestore.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bridgestore.R
import com.example.bridgestore.model.Product
import com.bumptech.glide.Glide
import com.example.bridgestore.ProductViewActivity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

class ProductListAdapter(private val productList: List<Product>,private val context: Context) :
    RecyclerView.Adapter<ProductListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val productImageView: ImageView = itemView.findViewById(R.id.productImageView)

        init {
            productImageView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (view.id == R.id.productImageView) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val product = productList[position]
                    navigateToProductView(product)
                }
            }
        }

        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text = product.price.toString()
            descriptionTextView.text = product.description
            // Load product image using Glide
            val decodedBytes = Base64.decode(product.imageUrl, Base64.DEFAULT)

            // Decode byte array into Bitmap
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            productImageView.setImageBitmap(bitmap)

            productImageView.setOnClickListener(){
                navigateToProductView(product)
            }
        }

        private fun navigateToProductView(product: Product) {
            val intent = Intent(context, ProductViewActivity::class.java).apply {
                putExtra("product", product.id)
            }
            context.startActivity(intent)
        }
    }
}