package com.example.instagramcloneapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import com.example.instagramcloneapp.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

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

        val changeImageButton = findViewById<TextView>(R.id.change_image_text_btn)

        changeImageButton.setOnClickListener {
            checker = "clicked"
            CropImage.activity().setAspectRatio(1, 1).start(this@AccountSettingsActivity)
        }

        editButton.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            findViewById<CircleImageView>(R.id.profile_image_view_frag).setImageURI(imageUri)
        }
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

    private fun uploadImageAndUpdateInfo() {

        when {
            imageUri == null -> Toast.makeText(this, "Please select image first.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(findViewById<EditText>(R.id.full_name_profile_frag).text.toString()) -> Toast.makeText(this, "Please write full name first", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(findViewById<EditText>(R.id.username_profile_frag).text.toString()) -> Toast.makeText(this, "Please write user name first", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(findViewById<EditText>(R.id.bio_profile_frag).text.toString()) -> Toast.makeText(this, "Please write your bio first", Toast.LENGTH_LONG).show()

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = findViewById<EditText>(R.id.full_name_profile_frag).text.toString().lowercase()
                        userMap["username"] = findViewById<EditText>(R.id.username_profile_frag).text.toString().lowercase()
                        userMap["bio"] = findViewById<EditText>(R.id.bio_profile_frag).text.toString().lowercase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Account Information has been updated successfully", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }
}