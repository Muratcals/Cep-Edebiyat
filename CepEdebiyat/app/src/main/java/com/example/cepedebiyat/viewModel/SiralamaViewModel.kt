package com.example.cepedebiyat.viewModel

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepedebiyat.model.Siralamalar
import com.example.cepedebiyat.unit.compain
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.model.MutableDocument

class SiralamaViewModel : ViewModel() {
    val items=MutableLiveData<List<Siralamalar>>()
    val error=MutableLiveData<Boolean>()
    val progress=MutableLiveData<Boolean>()
    fun getData(view: View){
        progress.value=true
        compain.db().collection("Sıralamalar").orderBy("Puan",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error!=null){
                Toast.makeText(view.context,error.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                if (!value!!.isEmpty){
                    val item =value.documents
                    val spare =ArrayList<Siralamalar>()
                    for (result in item){
                        val authName =result.get("Auth Name") as String
                        val profilImage=result.get("Profil Image") as String
                        val score =result.get("Puan") as Number
                        val gender =result.get("Cinsiyet") as String
                        spare.add(Siralamalar(score,authName,gender,profilImage))
                    }
                    items.value=spare
                }
            }
            progress.value=false
        }
    }
}