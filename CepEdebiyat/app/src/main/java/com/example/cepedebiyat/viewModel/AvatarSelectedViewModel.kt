package com.example.cepedebiyat.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepedebiyat.unit.compain

class AvatarSelectedViewModel : ViewModel() {

    val avatars=MutableLiveData<List<String>>()
    val error =MutableLiveData<Boolean>()
    val progress=MutableLiveData<Boolean>()

    //avatarları firebaseden çekeriz
    fun getAvatar(){
        progress.value=true
        val array =ArrayList<String>()
        compain.db().collection("Avatarlar").get().addOnSuccessListener {
            if (it.isEmpty){
                error.value=true
            }else{
                for (deger in it){
                    val avatar =deger.get("url") as String
                    array.add(avatar)
                }
                avatars.value=array
            }
        }
        progress.value=false
    }
}