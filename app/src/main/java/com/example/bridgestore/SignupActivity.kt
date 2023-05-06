package com.example.bridgestore

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bridgestore.model.UserModel
import com.example.bridgestore.R

class SignupActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var contactNumberEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var userRoleSpinner: Spinner
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button

    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText)
        contactNumberEditText = findViewById(R.id.contactNumberEditText)
        addressEditText = findViewById(R.id.addressEditText)
        userRoleSpinner = findViewById(R.id.userRoleSpinner)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)

        // Set click listener for the register button
        registerButton.setOnClickListener {
            performSignup()
        }
    }
    @SuppressLint("RestrictedApi")
    private  fun performSignup() {
        // Get input values
        val name = nameEditText.text.toString()
        val contactNumber = contactNumberEditText.text.toString()
        val address = addressEditText.text.toString()
        val userRole = "Buyer"
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        //validate
        if (name.isBlank() || contactNumber.isBlank() || address.isBlank() || email.isBlank() || password.isBlank()) {
            // Display an error message for empty fields
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            return
        }
        val user = UserModel(name, contactNumber, address, userRole, email, password)

        // Perform signup logic here
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Signup success, get the user
                    val firebaseUser = FirebaseAuth.getInstance().currentUser

                    // Additional signup logic, if needed

                    // Save user data to Firestore collection "users"
                    val userCollection = db.collection("users")
                    if (firebaseUser != null) {
                        userCollection.document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                // User data saved successfully
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                // Error saving user data
                                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }

                    // Example: Display a toast message indicating successful signup
                    Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()
                } else {
                    // Signup failed, display an error message
                    val errorMessage = task.exception?.message
                    Toast.makeText(this, "Signup failed: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
        // You can use the retrieved values to sign up the user using Firebase Authentication or any other backend service



    }


}