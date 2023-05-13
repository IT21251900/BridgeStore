package com.example.bridgestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.bridgestore.model.UserModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CustomerRegistrationActivity : AppCompatActivity() {
    lateinit var customerName:TextInputEditText
    lateinit var phoneNumber : TextInputEditText
    lateinit var email:TextInputEditText
    lateinit var password:TextInputEditText
    lateinit var cPassword:TextInputEditText
    lateinit var passwordWrapper:TextInputLayout
    lateinit var cPasswordWrapper :TextInputLayout
    lateinit var title:TextView
    lateinit var signupButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_registration)


        customerName  = findViewById(R.id.customerFullName)
        phoneNumber  = findViewById(R.id.customerPhone)
        email  = findViewById(R.id.customerEmail)
//        deleteButton = findViewById(R.id.customerDeleteButton)
        cPassword  = findViewById(R.id.customerConfirmPassword)
        password  = findViewById(R.id.customerPassword)
        signupButton = findViewById(R.id.customerSignUpButton)
        passwordWrapper = findViewById(R.id.passwordWrapper)
        cPasswordWrapper = findViewById(R.id.cpasswordWrapper)
        title = findViewById(R.id.tot)
        
        signupButton.setOnClickListener(){
            if(intent.getStringExtra("uid")!=null){
                updateUser()
            }else{
                signupUser()
            }
        }

        if(intent.getStringExtra("uid")!=null){
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener { value->
                if(value.exists()){
                    customerName.setText(value.getString("name"))
                    phoneNumber.setText(value.getString("contactNumber"))
                    email.setText(value.getString("email"))
                    email.isEnabled = false
                    passwordWrapper.visibility = View.GONE
                    cPasswordWrapper.visibility  = View.GONE
                    signupButton.text = "Update"
                    title.text = "Update Profile"
                }
            }
        }else{

        }



    }

    fun signupUser(){
        val name = customerName.text.toString().trim()
        val phone = phoneNumber.text.toString().trim()
        val emailStr = email.text.toString().trim()
        val passwordStr = password.text.toString().trim()
        val confirmPasswordStr = cPassword.text.toString().trim()

        if (name.isNotEmpty() && phone.isNotEmpty() && emailStr.isNotEmpty() && passwordStr.isNotEmpty() && confirmPasswordStr.isNotEmpty()) {
            if (passwordStr == confirmPasswordStr) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val u = UserModel(
                                name,
                                phone,
                                "address",
                                "Buyer",
                                emailStr,
                                passwordStr
                            )
                            FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                                .set(u)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Signup successfully.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, ProductsListActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Signup failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to sign up.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
        }
    }


    fun updateUser (){
        var u:UserModel = UserModel(
            customerName.text.toString(),
            phoneNumber.text.toString(),
            "address.text",
            "Buyer",
            email.text.toString(),
            "password.text.toString()"
        )
        FirebaseFirestore
            .getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid).set(u).addOnSuccessListener { va->
                FirebaseFirestore
                    .getInstance()
                    .collection("customers")
                    .document(FirebaseAuth.getInstance().currentUser!!.uid).set(u).addOnSuccessListener { va->
                        Toast.makeText(this,"Usr updated succesdfully",Toast.LENGTH_SHORT)
                        val intent = Intent(this, CustomerProfileActivity::class.java)
                        startActivity(intent)
                    }

            }
    }
}