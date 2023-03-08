package com.example.cepedebiyat.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepedebiyat.model.SaveModel
import com.example.cepedebiyat.unit.compain

class SaveViewModel : ViewModel() {

    val authorNameList=MutableLiveData<List<SaveModel>>()
    val progress=MutableLiveData<Boolean>()
    val error=MutableLiveData<Boolean>()
    fun getAuthorName(){
        progress.value=true
        compain.db().collection("${compain.auth().currentUser!!.email.toString()} Kaydedilenleri").get().addOnSuccessListener { values->
            if (values.size()==0){
                error.value=true
            }else{
                error.value=false
                val value=values.documents
                val array =ArrayList<SaveModel>()
                for (valuess in value){
                    val id=valuess.id
                    val authorName=valuess.get("Eser Adı") as String
                    val selectedButton=valuess.get("Secilen Şık Text") as String
                    compain.db().collection("Yazar-Eser Listesi").whereEqualTo("Eser Adı",authorName).get().addOnSuccessListener { state->
                        val workName =state.documents[0].get("Yazar Adı") as String
                        if (workName.equals(selectedButton)) {
                            array.add(SaveModel(id,selectedButton,authorName,true))
                        }
                        else {
                            array.add(SaveModel(id,selectedButton,authorName,false))
                        }
                        authorNameList.value=array
                    }.addOnFailureListener {
                        println(it.localizedMessage)
                    }
                }
            }
        }
        progress.value=false
    }
}