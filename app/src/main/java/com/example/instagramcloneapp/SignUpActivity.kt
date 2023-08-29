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

        //Here we catch our variable from the screen and we give a variable name
        val fullName = findViewById<EditText>(R.id.fullname_signup).text.toString()
        val userName = findViewById<EditText>(R.id.username_signup).text.toString()
        val email = findViewById<EditText>(R.id.email_signup).text.toString()
        val password = findViewById<EditText>(R.id.password_signup).text.toString()

        //When is the way to work if else here
        when {

            //We check if our values are empty and return a message to our user
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Full name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "User Name is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is required.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is required.", Toast.LENGTH_LONG).show()

            else -> {

                //This block is the modal that user will see meanwhile the backend do it process
                val progressDialog = ProgressDialog( this@SignUpActivity)
                progressDialog.setTitle("Sign Up")
                progressDialog.setMessage("Please wait, this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                //We call our FirebaseAuth tool
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                //This is our try catch because we check is the process did right and then we act in consequences
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    task -> if ( task.isSuccessful) {

                        /**If successful pass the variables to our next function **/
                        saveUserInfo(fullName, userName, email, progressDialog)
                    }
                    else {

                        /**If something went wrong we return a message to our user and turn off our Firebase tool, also hide our modal dialog**/
                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                        mAuth.signOut()
                        progressDialog.dismiss()
                    }
                }
            }
        }
    }

    //Here we have our backend function block
    private fun saveUserInfo( fullName: String, userName: String, email: String, progressDialog: ProgressDialog ) {

        //We ask for an user ID by firebase
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        //We notify our database that we will be working we the Users collection
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child( "Users")

        //Here we create a map variable, and pass the values that the user gave us to our object name array
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.lowercase()
        userMap["username"] = userName.lowercase()
        userMap["email"] = email
        userMap["bio"] = "Hey I am using Instagram Clone App."
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-clone-app-44d2c.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=39d51ed1-c69b-4c71-aec3-aa6da71697c5"

        //When we have all we need then this function will run
        usersRef.child(currentUserID).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                /**If is successful we hide the modal, and notify the user that we create the user, and then we clean the activity to start the new one **/
                progressDialog.dismiss()
                Toast.makeText(this, "Account has been created successfully", Toast.LENGTH_LONG).show()

                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else {

                /**If something went wrong we return a message to our user and turn off our Firebase tool, also hide our modal dialog**/
                val message = task.exception!!.toString()
                Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }
        }
    }
}