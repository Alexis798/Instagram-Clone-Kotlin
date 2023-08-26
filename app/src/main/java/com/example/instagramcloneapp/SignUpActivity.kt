package com.example.instagramcloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signin = findViewById<Button>(R.id.signin_link_btn)

        signin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}