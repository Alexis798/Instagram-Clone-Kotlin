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

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        //This is the way to refer to id inside layout and work it in backend
        val signup = findViewById<Button>(R.id.signup_link_btn)
        val loginButton = findViewById<Button>(R.id.login_btn)

        signup.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        loginButton.setOnClickListener {
            loginUser();
        }


    }

    private fun loginUser() {

        //Here we catch our variable from the screen and we give a variable name
        val email = findViewById<EditText>(R.id.email_login).text.toString()
        val password = findViewById<EditText>(R.id.password_login).text.toString()

        //When is the way to work if else here
        when {

            //We check if our values are empty and return a message to our user
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required.", Toast.LENGTH_LONG).show()

            else -> {

                //This block is the modal that user will see meanwhile the backend do it process
                val progressDialog = ProgressDialog( this@SignInActivity)
                progressDialog.setTitle("Login")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                //We call our FirebaseAuth tool
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    //This is our try catch because we check is the process did right and then we act in consequences
                    if (task.isSuccessful ) {
                        /**If successful we notify to change our context, hide our modal and start the new activity after clean the last one **/
                        progressDialog.dismiss()

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {

                        /**If something went wrong we return a message to our user and turn off our Firebase tool, also hide our modal dialog**/
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    //This is our default start function for this screen
    override fun onStart() {
        super.onStart()

        //we check if we have a user in our cache and if that the case we check it we firebase and login the user
        if (FirebaseAuth.getInstance().currentUser != null) {

            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }
    }
}