package com.example.bridgestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class RegisterOptionsActivity : AppCompatActivity() {

     lateinit var customerRegisterButton :Button
     lateinit var sellerRegisterButton :Button
     lateinit var loginButton:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_options)


        customerRegisterButton = findViewById(R.id.sellerSignupButton)
        sellerRegisterButton = findViewById(R.id.customerSignupButton)
        loginButton = findViewById(R.id.loginSingupButton)

        customerRegisterButton.setOnClickListener(){
            var intent = Intent(this,SellerRegistrationActivity::class.java)
            startActivity(intent)
        }
        sellerRegisterButton.setOnClickListener(){
            var intent = Intent(this,CustomerRegistrationActivity::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener(){
            finish()
        }
    }
}