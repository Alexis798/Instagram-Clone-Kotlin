package com.example.instagramcloneapp.Model

import android.media.Image

class User {

    //Here we declare the names of variable in the exact way we have in our firebase database
    private var username: String = ""
    private var fullname: String = ""
    private var bio: String = ""
    private var image: String = ""
    private var uid: String = ""

    constructor()

    //Here we do the assignation of our variables values
    constructor(username: String, fullname: String, bio: String, image: String, uid: String) {

        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.image = image
        this.uid = uid

    }

    //In this block we create the process to get the values of our variable and setting it
    fun getUsername() : String {

        return username
    }

    fun setUsername(username: String) {

        this.username = username
    }

    fun getFullname() : String {

        return fullname
    }

    fun setFullname(fullname: String) {

        this.fullname = fullname
    }

    fun getBio() : String {

        return bio
    }

    fun setBio(bio: String) {

        this.bio = bio
    }

    fun getImage() : String {

        return image
    }

    fun setImage(image: String) {

        this.image = image
    }

    fun getUID() : String {

        return uid
    }

    fun setUID(uid: String) {

        this.uid = uid
    }



}