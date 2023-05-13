package com.example.bridgestore
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton:Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupButton = findViewById(R.id.signUpButton2)
        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Perform Firebase sign-in with email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign-in success
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            // Get the user role from Firebase users collection
                            val userRole = getUserRoleFromFirebase(currentUser.uid)



                        }
                    } else {
                        // Sign-in failure
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        signupButton.setOnClickListener(){
            val intent = Intent(this, RegisterOptionsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getUserRoleFromFirebase(userId: String): String {

        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("users")
        val documentRef = usersCollection.document(userId)

        var userRole: String = "" // Define the user role variable

        documentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                userRole = documentSnapshot.getString("userRole") ?: ""
                print("------ user role is "+userRole)
                saveUserRoleInLocalStorage(userRole)
                val intent = Intent(this, ProductsListActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                // Continue with the rest of the logic
            } else {
                userRole = "" // Handle the case when the user document doesn't exist
            }
        }.addOnFailureListener { exception ->
            userRole = "" // Handle the failure case, e.g., error fetching data
        }

        return userRole
    }


    private fun saveUserRoleInLocalStorage(userRole: String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("UserRole", userRole)
        editor.apply()
    }
}
