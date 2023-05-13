package com.example.bridgestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class CustomerProfileActivity : AppCompatActivity() {


    lateinit var nameText:TextView
    lateinit var phoneText:TextView
    lateinit var emailText:TextView
    lateinit var updateButton :Button
    lateinit var deleteProfile:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_profile)

        nameText =findViewById(R.id.nameEt)
        phoneText = findViewById(R.id.phoneEt)
        emailText = findViewById(R.id.emailEt)
        updateButton = findViewById(R.id.updateBtn)
        deleteProfile = findViewById(R.id.deleteBtn)


        FirebaseFirestore.getInstance()
            .collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).get().addOnSuccessListener(){
                    s->
                if(s.exists()){
                    nameText.text = s.getString("name")
                    phoneText.text = s.getString("contactNumber")
                    emailText.text = s.getString("email")
                }
            }

        updateButton.setOnClickListener(){
            var intent = Intent(this,CustomerRegistrationActivity::class.java)
            intent.putExtra("uid",FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(intent)
        }


        deleteProfile.setOnClickListener(){
            AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete your profile?")
                .setPositiveButton("Delete") { dialog, _ ->
                    FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().currentUser!!.uid).delete()
                        .addOnCompleteListener() { _ ->
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}