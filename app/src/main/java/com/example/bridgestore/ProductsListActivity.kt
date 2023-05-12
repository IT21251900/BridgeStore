package com.example.bridgestore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bridgestore.adapters.ProductListAdapter
import com.example.bridgestore.model.Product
import com.example.bridgestore.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductsListActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productListAdapter: ProductListAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var profileButton:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products_list)

        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = LinearLayoutManager(this)
        swipeRefreshLayout = findViewById(R.id.productsViewRefresher)
        profileButton = findViewById(R.id.profilePicture)
        swipeRefreshLayout.setOnRefreshListener {
            db.collection("products")
                .get()
                .addOnSuccessListener { result ->
                    val productList = mutableListOf<Product>()
                    for (document in result) {
                        val product = document.toObject(Product::class.java)
                        product.id = document.id;
                        productList.add(product)
                    }
                    // Set product data to the RecyclerView using the ProductListAdapter
                    productListAdapter = ProductListAdapter(productList,this)
                    productRecyclerView.adapter = productListAdapter
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(this, "Failed to fetch product data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            swipeRefreshLayout.isRefreshing = false
        }
        // Fetch product data from Firestore
        
        
        if(getUserRoleFromLocalStorage() == "Seller"){
            db.collection("products")
                .whereEqualTo("sellerId", FirebaseAuth.getInstance().currentUser?.uid )
                .get()
                .addOnSuccessListener { result ->
                    val productList = mutableListOf<Product>()
                    for (document in result) {
                        val product = document.toObject(Product::class.java)
                        product.id = document.id;
                        productList.add(product)
                    }
                    // Set product data to the RecyclerView using the ProductListAdapter
                    productListAdapter = ProductListAdapter(productList,this)
                    productRecyclerView.adapter = productListAdapter
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(this, "Failed to fetch product data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
        else{
            db.collection("products")
                .get()
                .addOnSuccessListener { result ->
                    val productList = mutableListOf<Product>()
                    for (document in result) {
                        val product = document.toObject(Product::class.java)
                        product.id = document.id;
                        productList.add(product)
                    }
                    // Set product data to the RecyclerView using the ProductListAdapter
                    
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(this, "Failed to fetch product data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
        // Find the "Add Product" button by its ID
        val addProductButton = findViewById<Button>(R.id.addProductButton)

    // Set a click listener on the button
        addProductButton.setOnClickListener {
            // Create an Intent to navigate to the AddProductActivity
            val intent = Intent(this, AddProductActivity::class.java)

            // Start the AddProductActivity
            startActivity(intent)
        }

        

        profileButton.setOnClickListener(){
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener { value->
                if(value.exists()){
                    if(value.get("userRole") != "User"){
                        var intent = Intent(this,CustomerRegistrationActivity::class.java)
                        intent.putExtra("uid",value.id)
                        startActivity(intent)
                    }else{
                        var intent = Intent(this,SellerRegistrationActivity::class.java)
                        intent.putExtra("uid",value.id)
                        startActivity(intent)
                    }
                }
             }
        }
    }

    private fun getUserRoleFromLocalStorage(): String {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", "") ?: ""
    }
}
