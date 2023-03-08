package com.example.cepedebiyat.viewModel

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.example.cepedebiyat.MainActivity
import com.example.cepedebiyat.R
import com.example.cepedebiyat.unit.compain

class LoginViewModel : ViewModel() {
    val progressLogin =MutableLiveData<Boolean>()
    fun getAuthName(view: View,authName:String,password:String){
        progressLogin.value=true
        compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("Auth Name",authName).get().addOnCompleteListener { value->
            if (!value.isSuccessful){
                Toast.makeText(view.context,value.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                if (value.result.isEmpty){
                    Toast.makeText(view.context,"Böyle bir kullanıcı bulunamadı",Toast.LENGTH_SHORT).show()
                }else{
                    val eMail =value.result.documents[0].get("E-Mail") as String
                    compain.auth().signInWithEmailAndPassword(eMail,password).addOnSuccessListener {
                        Toast.makeText(view.context,"Kullanici Girişi başarılı",Toast.LENGTH_SHORT).show()
                        val intent =Intent(view.context,MainActivity::class.java)
                        startActivity(view.context,intent,null)
                        view.findNavController().navigate(R.id.action_loginFragment2_to_gameFragment2)
                    }
                }
            }
        }
        progressLogin.value=false
    }
}