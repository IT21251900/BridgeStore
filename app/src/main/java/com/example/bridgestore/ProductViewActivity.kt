package com.example.bridgestore

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.bridgestore.model.Product
import com.example.bridgestore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProductViewActivity : AppCompatActivity() {

    lateinit var  imageView: ImageView
    lateinit var editButton: Button
    lateinit var deleteButton: Button
    lateinit var contactSellerButton : Button
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_view)

      var pod:Product? = Product()


        val product = intent.getStringExtra("product")
        val userRole = getUserRoleFromLocalStorage()
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh logic here
            // Call a method to reload the product details or perform any necessary operations
            // For example, you can call a function like fetchProductDetails() to reload the data
            if( product !=null){
                refresh(product);
            }
        }
        //set buttons
        editButton = findViewById<Button>(R.id.updateProductButton)
        deleteButton = findViewById<Button>(R.id.deleteProductButton)
        imageView= findViewById(R.id.productImageView)
        contactSellerButton = findViewById(R.id.buyNowButton)


        FirebaseFirestore.getInstance().collection("users")
            .document(FirebaseAuth
                .getInstance().currentUser!!.uid).get()
            .addOnSuccessListener {
                value->
                    if(value.exists()){
                      if(value.getString("userRole") != "Buyer"){
                          editButton.visibility= View.GONE
                          deleteButton.visibility = View.GONE
                      }else{
                          contactSellerButton.visibility =View.GONE
                          val layoutParams = imageView.layoutParams
                          layoutParams.height = resources.getDimensionPixelSize(R.dimen.image_height)
                          imageView.layoutParams = layoutParams
                      }
                    }

        }


        // Set the product details in the views
        product?.let {
            val titleTextView: TextView = findViewById(R.id.productTitleTextView)
            val priceTextView: TextView = findViewById(R.id.productPriceTextView)
            val descriptionTextView: TextView = findViewById(R.id.productDescriptionTextView)

            FirebaseFirestore.getInstance().collection("products").document(product).get().addOnSuccessListener {
                    result ->
                if(result.exists()){
                    val p = result.toObject(Product::class.java)
                    pod = p

                    // Set the title
                    titleTextView.text = p?.name

                    // Set the price
                    priceTextView.text = "Rs " + p?.price.toString()

                    // Set the description
                    descriptionTextView.text = p?.description

                    // Load product image using Glide
                    val decodedBytes = Base64.decode(p?.imageUrl, Base64.DEFAULT)

                    // Decode byte array into Bitmap
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imageView.setImageBitmap(bitmap)
                }
            }



            
            editButton.setOnClickListener {
                if(pod!=null) {
                    // Create an intent to navigate to EditProductActivity
                    val intent = Intent(this, EditProductActivity::class.java)

                    // Pass the product model through the intent
                    intent.putExtra("pid",product )

                    // Start the EditProductActivity
                    startActivity(intent)
                }else{
                    print("------------ else called")
                }
            }



            // Set click listener for the "Buy Now" button
            contactSellerButton.setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java)
                intent.putExtra("sellerId",pod?.sellerId)
                startActivity(intent)
            }

            deleteButton.setOnClickListener(){
                FirebaseFirestore.getInstance().collection("products").document(product).delete().addOnCompleteListener(){
                    finish()
                }
            }
        }
    }
    private fun getUserRoleFromLocalStorage(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", "") ?: ""
    }

    private fun refresh(product:String){
        val titleTextView: TextView = findViewById(R.id.productTitleTextView)
        val priceTextView: TextView = findViewById(R.id.productPriceTextView)
        val descriptionTextView: TextView = findViewById(R.id.productDescriptionTextView)

        FirebaseFirestore.getInstance().collection("products").document(product).get().addOnSuccessListener {
                result ->
            if(result.exists()){
                val p = result.toObject(Product::class.java)
                // Set the title
                titleTextView.text = p?.name

                // Set the price
                priceTextView.text = "Rs " + p?.price.toString()

                // Set the description
                descriptionTextView.text = p?.description
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }
}