package com.example.bridgestore
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bridgestore.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class AddProductActivity : AppCompatActivity() {

    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var productImageView: ImageView
    private lateinit var addProductButton: Button

    private var selectedImageBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        productNameEditText = findViewById(R.id.productNameEditText)
        productPriceEditText = findViewById(R.id.productPriceTex)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        productImageView = findViewById(R.id.productImage)
        addProductButton = findViewById(R.id.addProductButtonf)
        addProductButton.text =
            "Add Product"
        productImageView.setOnClickListener {
            openImagePicker()
        }

        addProductButton.setOnClickListener {
            createProduct()
        }

    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            try {
                if(selectedImageUri!=null){
                    // Convert the selected image to Base64
                    selectedImageBase64 = convertImageToBase64(selectedImageUri)
                    // Set the selected image into the ImageView
                    productImageView.setImageURI(selectedImageUri)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                // Handle the exception appropriately (e.g., show an error message)
            }
        }
    }

    private fun convertImageToBase64(imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun createProduct() {
        val name = productNameEditText.text.toString().trim()
        val priceText = productPriceEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (name.isEmpty()) {
            productNameEditText.error = "Product name is required"
            return
        }

        if (priceText.isEmpty()) {
            productPriceEditText.error = "Product price is required"
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0) {
            productPriceEditText.error = "Invalid product price"
            return
        }

        val product = Product(
            name = name,
            price = price,
            description = description,
            imageUrl = selectedImageBase64 ?: "",
            sellerId = FirebaseAuth.getInstance().currentUser?.uid ?:""
        )


        val db = FirebaseFirestore.getInstance()
        db.collection("products")
            .add(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                navigateBackToProductsList()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 100
    }

    private fun navigateBackToProductsList() {
        val intent = Intent(this, ProductsListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
