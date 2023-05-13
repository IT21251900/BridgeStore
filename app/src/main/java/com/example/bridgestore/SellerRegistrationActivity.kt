package  com.example.bridgestore
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bridgestore.model.Product
import com.example.bridgestore.model.Seller
import com.example.bridgestore.model.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class SellerRegistrationActivity : AppCompatActivity() {

    private lateinit var sellerFullName: TextInputEditText
    private lateinit var sellerPhone: TextInputEditText
    private lateinit var sellerAddress: TextInputEditText
    private lateinit var sellerEmail: TextInputEditText
    private lateinit var sellerBusinessName: TextInputEditText
    private lateinit var sellerBusinessRegistrationNumber: TextInputEditText
    private lateinit var sellerDescription: TextInputEditText
    private lateinit var sellerPassword: TextInputEditText
    private lateinit var sellerConfirmPassword: TextInputEditText
    private lateinit var sellerSignUpButton: Button
    private lateinit var pLayout: TextInputLayout
    private lateinit var cpLayout: TextInputLayout
    private lateinit var text1:TextView
    private lateinit var text2:TextView
    private lateinit var signUpText:TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_registation)

        sellerFullName = findViewById(R.id.sellerFullName)
        sellerPhone = findViewById(R.id.sellerPhone)
        sellerAddress = findViewById(R.id.sellerAddress)
        sellerEmail = findViewById(R.id.sellerEmail)
        sellerBusinessName = findViewById(R.id.sellerBusinessName)
        sellerBusinessRegistrationNumber = findViewById(R.id.sellerBusinessRegistrationNumber)
        sellerDescription = findViewById(R.id.sellerDescription)
        sellerPassword = findViewById(R.id.sellerPassword)
        sellerConfirmPassword = findViewById(R.id.sellerConfirmPassword)
        sellerSignUpButton = findViewById(R.id.sellerSignUpButton)
        pLayout = findViewById(R.id.sellerPasswordLayout)
        cpLayout = findViewById(R.id.sellerConfirmPasswordLayout)
        text1  = findViewById(R.id.alreadyHaveAccount)
        text2 = findViewById(R.id.loginText)
        signUpText = findViewById(R.id.signUpTitle)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
//        deleteButton = findViewById(R.id.sellerDeleteButton)

        if(intent.getStringExtra("uid") !=null){
            signUpText.text = "Update Profile"
        }

        if(intent.getStringExtra("uid") !=null){
            db.collection("sellers").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener { value->
                if(value.exists()){
                        sellerFullName.setText(value.getString("fullName"))
                        sellerPhone.setText(value.getString("phone"))
                        sellerAddress.setText(value.getString("address"))
                        sellerEmail.setText(value.getString("email"))
                        sellerBusinessName.setText(value.getString("businessName"))
                        sellerBusinessRegistrationNumber.setText(value.getString("businessRegistrationNumber"))
                        sellerDescription.setText(value.getString("description"))
                        sellerPassword.visibility = View.GONE
                        sellerConfirmPassword.visibility = View.GONE
                        pLayout.visibility = View.GONE
                        cpLayout.visibility = View.GONE
                        text1.visibility = View.GONE
                        text2.visibility = View.GONE
                        sellerSignUpButton.setText("Update")
                }
            }
        }else{

        }

        sellerSignUpButton.setOnClickListener {
            val fullName = sellerFullName.text.toString()
            val phone = sellerPhone.text.toString()
            val address = sellerAddress.text.toString()
            val email = sellerEmail.text.toString()
            val businessName = sellerBusinessName.text.toString()
            val businessRegistrationNumber = sellerBusinessRegistrationNumber.text.toString()
            val description = sellerDescription.text.toString()
            val password = sellerPassword.text.toString()

            val seller = Seller(
                id = "", // Firestore will auto-generate the ID
                fullName = fullName,
                phone = phone,
                address = address,
                email = email,
                businessName = businessName,
                businessRegistrationNumber = businessRegistrationNumber,
                description = description
            )
             val firestore = FirebaseFirestore.getInstance()
            val updatedSeller = seller.updateSeller(
                fullName = fullName,
                phone = phone,
                address = address
                // ... other fields
            )
            if(intent.getStringExtra("uid")!=null){
                firestore.collection("sellers").document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .set(updatedSeller)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Updated succesfully",Toast.LENGTH_SHORT)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this,"Updated succesfully",Toast.LENGTH_SHORT)
                    }
            }else{
                signUpSeller(email, password, seller)
            }
        }


    }

    private fun signUpSeller(email: String, password: String, seller: Seller) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val userCollection = db.collection("users")
                    if (firebaseUser != null) {
                        val user = UserModel(seller.fullName, seller.phone, seller.address, "Seller", email, password)
                        userCollection.document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {

                                saveSellerToFirestore(seller,firebaseUser.uid)
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                // Error saving user data
                                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                } else {
                    // Sign up failed
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun saveSellerToFirestore(seller: Seller, id:String) {
        db.collection("sellers").document(id)
            .set(seller)
            .addOnSuccessListener { _ ->
                Toast.makeText(baseContext, "Seller registration successful.", Toast.LENGTH_SHORT).show()
                var intent = Intent(this,ProductsListActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(baseContext, "Error adding seller: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
