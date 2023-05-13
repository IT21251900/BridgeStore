package com.example.bridgestore

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.bridgestore.model.Product
import com.google.firebase.firestore.FirebaseFirestore


class EditProductActivity : AppCompatActivity() {
    private lateinit var product: String
    private lateinit var imageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var updateButton: Button
    private val firestore = FirebaseFirestore.getInstance()
      var imageUrl:String = ""
    private  var sellerId:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        // Retrieve the product model from the intent
        product = intent.getStringExtra("pid")!!


       imageView = findViewById(R.id.productImageView)
        nameEditText = findViewById(R.id.nameEditText)
        priceEditText = findViewById(R.id.priceEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        updateButton = findViewById(R.id.updateButton)

        print("view find  sucess")
     FirebaseFirestore.getInstance().collection("products").document(product).get().addOnSuccessListener { value->// Set initial values for the views
            // Load product image using Glide
          if(value.exists())  {
             val decodedBytes = Base64.decode(value.getString("imageUrl"), Base64.DEFAULT)

                // Decode byte array into Bitmap
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
               imageView.setImageBitmap(bitmap)

                 nameEditText.setText(value.getString("name"))
               priceEditText.setText(value.get("price").toString())
               descriptionEditText.setText(value.getString("description"))
             imageUrl = value.getString("imageUrl") ?: ""
             sellerId = value.getString("sellerId") ?:""
               print("data set success")
               // Set click listener for the image view to pick image
               imageView.setOnClickListener {
                   // Open image picker
                   openImagePicker()
               }
            }}
        print("on click  success")
        // Set click listener for the update button
        updateButton.setOnClickListener {
            // Perform the necessary actions to update the product
            updateProduct()
        }
        print("update button intent success")
    }

    private fun openImagePicker() {
        // TODO: Implement image picker logic
    }

    private fun updateProduct() {
        val name = nameEditText.text.toString().trim()
        val price = priceEditText.text.toString().toDoubleOrNull()
        val description = descriptionEditText.text.toString().trim()

        // Validate the input fields
        if (name.isEmpty() || price == null || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Show a progress dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Updating product...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Update the product details in Firestore
        val updatedProduct = Product(product,name, price, description, imageUrl, true, sellerId)
        val productRef = firestore.collection("products").document(product?:"")
        productRef.set(updatedProduct)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to update product: ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }


}