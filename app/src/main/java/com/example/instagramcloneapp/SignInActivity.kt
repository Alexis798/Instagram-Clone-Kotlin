package com.example.instagramcloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //This is the way to refer to id inside layout and work it in backend
        val signup = findViewById<Button>(R.id.signup_link_btn)

        signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}