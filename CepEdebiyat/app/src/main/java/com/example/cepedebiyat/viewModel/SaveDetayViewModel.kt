package com.example.cepedebiyat.viewModel

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentSaveDetayBinding
import com.example.cepedebiyat.model.SaveModelDetails
import com.example.cepedebiyat.unit.compain

class SaveDetayViewModel : ViewModel() {

    val item =MutableLiveData<SaveModelDetails>()
    val progress =MutableLiveData<Boolean>()

    fun getQuestionDetail(id:String){
        progress.value=true
        compain.db().collection("${compain.auth().currentUser!!.email.toString()} Kaydedilenleri").document(id).get().addOnSuccessListener {
            if (it!=null){
                val workName=it.get("Eser Adı") as String
                val selectedItem=it.get("Secilen Şık Text") as String
                val item1=it.get("Şık1") as String
                val item2=it.get("Şık2") as String
                val item3=it.get("Şık3") as String
                val item4=it.get("Şık4") as String
                item.value= SaveModelDetails(workName,selectedItem,item1,item2,item3,item4)
            }
        }
        progress.value=false
    }

    fun questionDelete(view:View,id:String){
        progress.value=true
        compain.db().collection("${compain.auth().currentUser!!.email.toString()} Kaydedilenleri").document(id).delete().addOnSuccessListener {
            Toast.makeText(view.context,"Kaydedilen eser başarıyla silindi",Toast.LENGTH_SHORT).show()
        }
        progress.value=false
    }
    fun questionStateControl(selectedStylishText:String,binding: FragmentSaveDetayBinding,workName:String){
        compain.db().collection("Yazar-Eser Listesi").whereEqualTo("Eser Adı",workName).get().addOnSuccessListener {
            val values =it.documents
            val authorName=values[0].get("Yazar Adı") as String
            if (binding.saveStylish.text.equals(authorName)) questionBindingDesign(selectedStylishText,binding,0)
            if (binding.saveStylish1.text.equals(authorName)) questionBindingDesign(selectedStylishText,binding,1)
            if (binding.saveStylish2.text.equals(authorName)) questionBindingDesign(selectedStylishText,binding,2)
            if (binding.saveStylish3.text.equals(authorName)) questionBindingDesign(selectedStylishText,binding,3)
        }
    }
    fun questionBindingDesign(selectedStylishText:String,binding: FragmentSaveDetayBinding,rightStylish:Int){
        if (rightStylish==0) {
            binding.saveStylish.setBackgroundResource(R.drawable.selected_true_sahpe)
        }
        else {
            if (binding.saveStylish.text.toString().equals(selectedStylishText)){
                binding.saveStylish.setBackgroundResource(R.drawable.save_selected_shape)
            }else{
                binding.saveStylish.setBackgroundResource(R.drawable.selected_false_shape)
            }
        }
        if (rightStylish==1) {
            binding.saveStylish1.setBackgroundResource(R.drawable.selected_true_sahpe)
        }
        else {
            if (binding.saveStylish1.text.toString().equals(selectedStylishText)){
                binding.saveStylish1.setBackgroundResource(R.drawable.save_selected_shape)
            }else{
                binding.saveStylish1.setBackgroundResource(R.drawable.selected_false_shape)
            }
        }
        if (rightStylish==2) {
            binding.saveStylish2.setBackgroundResource(R.drawable.selected_true_sahpe)
        }
        else {
            if (binding.saveStylish2.text.toString().equals(selectedStylishText)){
                binding.saveStylish2.setBackgroundResource(R.drawable.save_selected_shape)
            }else{
                binding.saveStylish2.setBackgroundResource(R.drawable.selected_false_shape)
            }
        }
        if (rightStylish==3) {
            binding.saveStylish3.setBackgroundResource(R.drawable.selected_true_sahpe)
        }
        else {
            if (binding.saveStylish3.text.toString().equals(selectedStylishText)){
                binding.saveStylish3.setBackgroundResource(R.drawable.save_selected_shape)
            }else{
                binding.saveStylish3.setBackgroundResource(R.drawable.selected_false_shape)
            }
        }
    }
}