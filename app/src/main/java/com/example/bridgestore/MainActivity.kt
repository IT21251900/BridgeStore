package com.example.bridgestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.content.Intent
import com.example.bridgestore.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private val SPLASH_DELAY = 2000L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        Handler().postDelayed({
            navigateToScreenTwo()
        }, SPLASH_DELAY)
    }

    private fun navigateToScreenTwo() {
        // Start the activity with animated transition
        if(FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this, ProductsListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }else{
            val intent = Intent(this, SecondStartScreen::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }
    }
}