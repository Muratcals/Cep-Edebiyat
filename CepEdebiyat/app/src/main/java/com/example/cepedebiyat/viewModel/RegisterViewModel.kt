package com.example.cepedebiyat.viewModel

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepedebiyat.MainActivity
import com.example.cepedebiyat.databinding.FragmentRegisterBinding
import com.example.cepedebiyat.unit.compain
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.ActionCodeSettings

class RegisterViewModel : ViewModel() {

    val error1 =MutableLiveData<Boolean>()
    val error2 =MutableLiveData<Boolean>()
    val progressBar =MutableLiveData<Boolean>()
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    fun passwordControl(password:String){
        val numbers ="0123456789"
        val letters ="ABCDEFGHIİJKLMNOÖPRSUÜVYZ"
        val numberControl =numbers.filter {
            password.contains(it)
        }
        val letterControl=letters.filter {
            password.contains(it)
        }
       if (letterControl.isEmpty() || numberControl.isEmpty() ){
           error1.value=true
       }else{
           error1.value=false
       }
    }

    fun replayPasswordControl(pas1:String,pas2:String){
        if (pas1.equals(pas2)){
            error2.value=false
        }else{
            error2.value=true
        }
    }
    fun registerAuth(activity: Activity,state:String,view:View,binding: FragmentRegisterBinding){
        progressBar.value=true
        val name =binding.registerName.text.toString()
        val lastName=binding.registerLastName.text.toString()
        val authName=binding.registerAuthName.text.toString()
        val eMail=binding.registerEmail.text.toString()
        var cinsiyet :String
        if (binding.registerKadin.isChecked){
            cinsiyet=binding.registerKadin.text.toString()
        }else {
            cinsiyet=binding.registerErkek.text.toString()
        }
        val hashmap = hashMapOf<String,Any>()
        hashmap.put("Name",name)
        hashmap.put("Last Name",lastName)
        hashmap.put("Auth Name",authName)
        hashmap.put("E-Mail",eMail)
        hashmap.put("Cinsiyet",cinsiyet)
        hashmap.put("Profil Image","")
        if (state.equals("Register")){
            val password =binding.registerPassword.text.toString()
            hashmap.put("Giriş Yolu","Register")
            if (password.length<=6){
                Toast.makeText(view.context,"Şifreniz 6 karakterden uzun olmalı",Toast.LENGTH_SHORT).show()
                progressBar.value=false
            }else{
                registerAuthDatabase(state,binding,activity,view,cinsiyet,hashmap)
            }
        }else{
            hashmap.put("Giriş Yolu","Google")
            registerAuthDatabase(state,binding,activity,view,cinsiyet,hashmap)
        }
    }

    fun registerArragment(girisYolu:String,binding: FragmentRegisterBinding,activity: Activity,view: View,gender:String){
        mGoogleSignInClient=GoogleSignIn.getClient(activity,compain.gso)
        val hashmap= hashMapOf<String,Any>()
        hashmap.put("Cinsiyet",gender)
        hashmap.put("Puan",0)
        hashmap.put("Dogru Sayisi",0)
        hashmap.put("Toplam Soru",0)
        hashmap.put("Auth Name",binding.registerAuthName.text.toString())
        hashmap.put("E-Mail",binding.registerEmail.text.toString())
        hashmap.put("Profil Image","")
        compain.db().collection("Sıralamalar").add(hashmap).addOnSuccessListener {
            if (girisYolu.equals("Register")){
                compain.auth().createUserWithEmailAndPassword(binding.registerEmail.text.toString(),binding.registerPassword.text.toString()).addOnSuccessListener {
                   compain.auth().signOut()
                }
            }
            val intent =Intent(view.context,MainActivity::class.java)
            startActivity(view.context,intent,null)
            activity.finish()
            Toast.makeText(view.context,"Kaydınız Başarıyla oluşturulmuştur",Toast.LENGTH_SHORT).show()
        }
    }

    fun registerAuthDatabase(girisYolu: String,binding: FragmentRegisterBinding,activity: Activity,view:View,cinsiyet:String,hashMap: HashMap<String,Any>){
        compain.db().collection("Kullanıcı Bilgileri").add(hashMap).addOnSuccessListener {
            registerArragment(girisYolu,binding,activity,view,cinsiyet)
        }
    }

    fun eMaiiSent(view: View,binding: FragmentRegisterBinding){
        val actionCodeSettings = ActionCodeSettings.newBuilder().setUrl("https://cepedebiyat.page.link").setHandleCodeInApp(true).setDynamicLinkDomain("cepedebiyat.page.link").build()
        compain.auth().sendSignInLinkToEmail(binding.registerEmail.text.toString(),actionCodeSettings).addOnSuccessListener {
            Toast.makeText(view.context,"E mail doğrulama bağlantısı gönderildi.",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(view.context,"ali ata bak ${it.localizedMessage}",Toast.LENGTH_SHORT).show()
            println(it.localizedMessage?.toString())
        }
    }
    fun eMailLogin(view: View,binding: FragmentRegisterBinding){
        val intent = Intent()
        val emailLink =intent.data.toString()
        if (compain.auth().isSignInWithEmailLink(emailLink)){
            val email =binding.registerEmail.text.toString()
            compain.auth().signInWithEmailLink(email,emailLink).addOnSuccessListener {
                println(it.user!!.email.toString())
            }.addOnFailureListener {
                Toast.makeText(view.context,"ali bak ${it.localizedMessage}",Toast.LENGTH_SHORT).show()
                println(it.localizedMessage?.toString())
            }
        }
    }
}