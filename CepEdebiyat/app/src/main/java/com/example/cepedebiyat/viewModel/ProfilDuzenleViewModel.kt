package com.example.cepedebiyat.viewModel

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentProfilDuzenleBinding
import com.example.cepedebiyat.model.AuthInformation
import com.example.cepedebiyat.unit.compain
import com.example.cepedebiyat.view.ProfilDuzenleFragment
import com.example.cepedebiyat.view.ProfilFragment
import com.google.android.gms.auth.api.Auth

class ProfilDuzenleViewModel : ViewModel() {

    val information =MutableLiveData<AuthInformation>()
    val progress =MutableLiveData<Boolean>()
    fun getProfilInformation(){
        progress.value=true
        compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",compain.auth().currentUser!!.email.toString()).get().addOnCompleteListener {
            if (it.isSuccessful){
                val value =it.result.documents
                val name =value[0].get("Name") as String
                val lastName=value[0].get("Last Name") as String
                val authName =value[0].get("Auth Name") as String
                val eMail =value[0].get("E-Mail") as String
                val cinsiyet =value[0].get("Cinsiyet") as String
                information.value=AuthInformation(name,lastName,eMail,authName,cinsiyet)
                progress.value=false
            }
        }
    }
    fun updateInformation(authNames:String,binding: FragmentProfilDuzenleBinding,view: View){
        progress.value=true
        editItemIsCheckedControl(false,binding)
        var cinsiyet ="Erkek"
        val authName=binding.editAuthName.text.toString()
        if (binding.editKadin.isChecked){
            cinsiyet=binding.editKadin.text.toString()
        }else{
            cinsiyet=binding.editErkek.text.toString()
        }
        if (!authNames.equals(authName)){
            compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("Auth Name",authName).get().addOnSuccessListener {
                if(it.isEmpty){
                    updateProfil(view,binding,cinsiyet)
                }else{
                    Toast.makeText(view.context,"Bu kullanıcı adı alınmıştır",Toast.LENGTH_SHORT).show()
                    progress.value=false
                    editItemIsCheckedControl(true,binding)
                }
            }
        }else{
            updateProfil(view,binding,cinsiyet)
        }

    }

    fun editItemIsCheckedControl(state:Boolean,binding: FragmentProfilDuzenleBinding){
        binding.editButton.isEnabled=state
        binding.editLastName.isEnabled=state
        binding.editAuthName.isEnabled=state
        binding.radioButton.isEnabled=state
        binding.editLinear.isEnabled=state
    }

    fun updateProfil(view: View,binding: FragmentProfilDuzenleBinding,cinsiyet:String){
        val hashmap = hashMapOf<String,Any>()
        hashmap.put("Name",binding.editName.text.toString())
        hashmap.put("Last Name",binding.editLastName.text.toString())
        hashmap.put("Auth Name",binding.editAuthName.text.toString())
        hashmap.put("Cinsiyet",cinsiyet)
        compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",compain.auth().currentUser!!.email.toString()).get().addOnSuccessListener { kullaniciId->
            val valueId =kullaniciId.documents[0].id
            compain.db().collection("Kullanıcı Bilgileri").document(valueId).update(hashmap).addOnSuccessListener {
                compain.db().collection("Sıralamalar").whereEqualTo("E-Mail",compain.auth().currentUser!!.email.toString()).get().addOnSuccessListener { siralamaId->
                    val id =siralamaId.documents[0].id
                    val hashmapSiralama= hashMapOf<String,Any>()
                    hashmapSiralama.put("Auth Name",binding.editAuthName.text.toString())
                    hashmapSiralama.put("Cinsiyet",cinsiyet)
                    compain.db().collection("Sıralamalar").document(id).update(hashmapSiralama).addOnSuccessListener {
                        Toast.makeText(view.context,"Profil Başarıyla Güncellendi",Toast.LENGTH_SHORT).show()
                    }
                }
                progress.value=false
                editItemIsCheckedControl(true,binding)
                getProfilInformation()
            }
        }
    }
}