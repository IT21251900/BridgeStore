package com.example.bridgestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class SecondStartScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_start_screen)
        // Button 1 click listener
        val button1 = findViewById<Button>(R.id.customerLoginButton)
        button1.setOnClickListener {
            navigateToLogin()
        }

        // Button 2 click listener
        val button2 = findViewById<Button>(R.id.sellerLoginButton)
        button2.setOnClickListener {
            navigateToLogin()
        }

        var signupButton = findViewById<TextView>(R.id.signUpButton)

        signupButton.setOnClickListener{
            navigateToSignup()
        }
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToSignup(){
        val intent = Intent(this, RegisterOptionsActivity::class.java)
        startActivity(intent)
    }
}