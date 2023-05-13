package com.example.bridgestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SellerProfileActivity : AppCompatActivity() {
    lateinit var userName:TextView
    lateinit var mobileNumber:TextView
    lateinit var address:TextView
    lateinit var email:TextView
    lateinit var businessName:TextView
    lateinit var businessNumber:TextView
    lateinit var businessDescription:TextView
    lateinit var updateButton:AppCompatButton
    lateinit var deeteButton:AppCompatButton
    var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_profile)

        userName = findViewById(R.id.nameEt)
        mobileNumber = findViewById(R.id.phoneEt)
        address = findViewById(R.id.addressEt)
        email = findViewById(R.id.emailEt)
        businessName =findViewById(R.id.businessnameEt)
        businessNumber = findViewById(R.id.businessregnoEt)
        businessDescription = findViewById(R.id.businessdescripEt)
        updateButton = findViewById(R.id.updateBtn)
        deeteButton = findViewById(R.id.deleteBtn)



        updateButton.setOnClickListener(){
            var intent = Intent(this,SellerRegistrationActivity::class.java)
            intent.putExtra("uid",FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(intent)
            finish()
        }

        deeteButton.setOnClickListener(){
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .delete()
            FirebaseAuth.getInstance().signOut()
            var intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()

        }


        db.collection("sellers").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener { value->
            if(value.exists()){
                userName.text = value.getString("fullName")
                mobileNumber.text = value.getString("phone")
                address.text = value.getString("address")
                email.text = value.getString("email")
                businessName.text = value.getString("businessName")
                businessNumber.text = value.getString("businessRegistrationNumber")
                businessDescription.text = value.getString("description")
            }else{
                userName.text= "No User FOund"
            }
        }
    }
}