package com.example.instagramcloneapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.example.instagramcloneapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val logoutButton = findViewById<Button>(R.id.logout_btn)
        val editButton = findViewById<ImageView>(R.id.save_info_profile_btn)

        //Here we create the process to logout our app
        logoutButton.setOnClickListener {
            //We use firebase to destroy the session
            FirebaseAuth.getInstance().signOut()

            //And them we notify our app to clean our context and bring the new activity
            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        editButton.setOnClickListener {
            if (checker == "clicked") {

            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    private fun updateUserInfoOnly() {

        if (TextUtils.isEmpty(findViewById<EditText>(R.id.full_name_profile_frag).text.toString())) {
            Toast.makeText(this, "Please write full name first", Toast.LENGTH_LONG).show()
        }

        if (TextUtils.isEmpty(findViewById<EditText>(R.id.username_profile_frag).text.toString())) {
            Toast.makeText(this, "Please write user name first", Toast.LENGTH_LONG).show()
        }

        if (TextUtils.isEmpty(findViewById<EditText>(R.id.bio_profile_frag).text.toString())) {
            Toast.makeText(this, "Please write your bio first", Toast.LENGTH_LONG).show()
        }

        else {

            val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

            val userMap = HashMap<String, Any>()
            userMap["fullname"] = findViewById<EditText>(R.id.full_name_profile_frag).text.toString().lowercase()
            userMap["username"] = findViewById<EditText>(R.id.username_profile_frag).text.toString().lowercase()
            userMap["bio"] = findViewById<EditText>(R.id.bio_profile_frag).text.toString().lowercase()

            usersRef.child(firebaseUser.uid).updateChildren(userMap)

            Toast.makeText(this, "Account Information has been updated successfully", Toast.LENGTH_LONG).show()

            val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun userInfo() {

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()){

                    val user = p0.getValue<User>(User::class.java)

                    val profileImg = findViewById<CircleImageView>(R.id.profile_image_view_frag)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImg)
                    findViewById<EditText>(R.id.username_profile_frag).setText(user!!.getUsername())
                    findViewById<EditText>(R.id.full_name_profile_frag).setText(user!!.getFullname())
                    findViewById<EditText>(R.id.bio_profile_frag).setText(user!!.getBio())

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}