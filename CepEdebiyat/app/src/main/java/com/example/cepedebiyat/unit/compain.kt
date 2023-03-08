package com.example.cepedebiyat.unit

import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object compain {
    fun db() = FirebaseFirestore.getInstance()
    fun auth() = FirebaseAuth.getInstance()
    fun storage() = FirebaseStorage.getInstance()
    var gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("747936736485-m1jhfhbmplugved5d05i587sl7gu2m5d.apps.googleusercontent.com")
        .requestEmail()
        .build()

}