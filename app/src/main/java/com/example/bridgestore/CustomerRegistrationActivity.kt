package com.example.bridgestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.bridgestore.R
import com.example.bridgestore.model.Customer
import com.example.bridgestore.model.Seller
import com.example.bridgestore.model.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CustomerRegistrationActivity : AppCompatActivity() {

    private lateinit var customerFullName: TextInputEditText
    private lateinit var customerPhone: TextInputEditText
    private lateinit var customerEmail: TextInputEditText
    private lateinit var customerPassword: TextInputEditText
    private lateinit var customerConfirmPassword: TextInputEditText
    private lateinit var customerSignUpButton: Button
    private lateinit var pLayout: TextInputLayout
    private lateinit var cpLayout: TextInputLayout
    private lateinit var text1: TextView
    private lateinit var text2: TextView
    private lateinit var signUpText: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var deleteButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_registration)

        customerFullName = findViewById(R.id.customerFullName)
        customerPhone = findViewById(R.id.customerPhone)
        customerEmail = findViewById(R.id.customerEmail)
        customerPassword = findViewById(R.id.customerPassword)
        customerConfirmPassword = findViewById(R.id.customerConfirmPassword)
        customerSignUpButton = findViewById(R.id.customerSignUpButton)
        pLayout = findViewById(R.id.customerPasswordLayout)
        cpLayout = findViewById(R.id.customerConfirmPasswordLayout)
        text1  = findViewById(R.id.alreadyHaveAccount)
        text2 = findViewById(R.id.loginText)
        signUpText = findViewById(R.id.signUpTitle)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        //deleteButton = findViewById(R.id.sellerDeleteButton)

        if(intent.getStringExtra("uid") !=null){
            signUpText.setText("Update Profile")
            db.collection("customers").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener { value->
                if(value.exists()){
                    customerFullName.setText(value.getString("fullName"))
                    customerPhone.setText(value.getString("phone"))
                    customerEmail.setText(value.getString("email"))
                    customerPassword.visibility = View.GONE
                    customerConfirmPassword.visibility = View.GONE
                    pLayout.visibility = View.GONE
                    cpLayout.visibility = View.GONE
                    text1.visibility = View.GONE
                    text2.visibility = View.GONE
                    customerSignUpButton.setText("Update")
                }
            }
        }else{
          //  deleteButton.visibility = View.GONE
        }

        customerSignUpButton.setOnClickListener {
            val fullName = customerFullName.text.toString()
            val phone = customerPhone.text.toString()
            val email = customerEmail.text.toString()
            val password = customerPassword.text.toString()

            val customer = Customer(
                "", // Firestore will auto-generate the ID
                fullName,
                phone,
                email,
                ""
            )
            val firestore = FirebaseFirestore.getInstance()

            if(intent.getStringExtra("uid")!=null){
                firestore.collection("sellers").document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .set(customer)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Updated succesfully", Toast.LENGTH_SHORT)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this,"Updated succesfully", Toast.LENGTH_SHORT)
                    }
            }else{
                signUpCustomer(email, password, customer)
            }
        }

        /*deleteButton.setOnClickListener(){
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).delete().addOnSuccessListener { value->
                FirebaseFirestore.getInstance().collection("customers").document(FirebaseAuth.getInstance().currentUser!!.uid).delete().addOnSuccessListener { value->
                    FirebaseAuth.getInstance().signOut()
                    var intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }*/
    }

    private fun signUpCustomer(email: String, password: String, customer: Customer) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val userCollection = db.collection("users")
                    if (firebaseUser != null) {
                        val user = UserModel(customer.fullName, customer.phone, "Customer", email, password)
                        userCollection.document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {

                                saveCustomerToFirestore(customer,firebaseUser.uid)
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


    private fun saveCustomerToFirestore(customer: Customer, id:String) {
        db.collection("sellers").document(id)
            .set(customer)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(baseContext, "Customer registration successful.", Toast.LENGTH_SHORT).show()
                var intent = Intent(this,ProductsListActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(baseContext, "Error adding customer: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}


