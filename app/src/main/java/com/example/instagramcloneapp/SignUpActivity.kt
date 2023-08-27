package com.example.instagramcloneapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signin = findViewById<Button>(R.id.signin_link_btn)
        val sigupButton = findViewById<Button>(R.id.signup_btn)

        signin.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        sigupButton.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount() {

        val fullName = findViewById<EditText>(R.id.fullname_signup).text.toString()
        val userName = findViewById<EditText>(R.id.username_signup).text.toString()
        val email = findViewById<EditText>(R.id.email_signup).text.toString()
        val password = findViewById<EditText>(R.id.password_signup).text.toString()

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Full name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "User Name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog( this@SignUpActivity)
                progressDialog.setTitle("Sign Up")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    task -> if ( task.isSuccessful) {
                        saveUserInfo(fullName, userName, email, progressDialog)
                    }
                    else {
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    private fun saveUserInfo( fullName: String, userName: String, email: String, progressDialog: ProgressDialog ) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child( "Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["email"] = email
        userMap["bio"] = "Hey I am using Instagram Clone App."
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-clone-app-44d2c.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=39d51ed1-c69b-4c71-aec3-aa6da71697c5"

        usersRef.child(currentUserID).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                Toast.makeText(this, "Account has been created successfully", Toast.LENGTH_LONG).show()

                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else {
                val message = task.exception!!.toString()
                Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }
        }
    }
}