package com.example.cepedebiyat.viewModel

import android.os.CountDownTimer
import android.view.View
import android.view.animation.Animation
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepedebiyat.GameActivity
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.ActivityGameBinding
import com.example.cepedebiyat.model.Eserler
import com.example.cepedebiyat.unit.compain
import kotlin.random.Random

class GameStartViewModel(val binding: ActivityGameBinding): ViewModel() {

    val eserler = MutableLiveData<ArrayList<Eserler>>()
    var id :String?=null
    fun startSayac(random: Int,buttonAnim:Animation): CountDownTimer {
        binding.gameSave.visibility=View.INVISIBLE
        binding.gameSave.isClickable=true
        val sayac1=object : CountDownTimer(10000,1000){
            override fun onTick(millisUntilFinished: Long) {
                println(millisUntilFinished)
            }
            override fun onFinish() {
                enabledButton(false)
                selectedButton(random)
                selectedButton(random)
                binding.gameSayac.cancelAnimation()
                binding.nextQuestion.visibility=View.VISIBLE
                binding.nextQuestion.animation=buttonAnim
            }
        }
        return sayac1
    }
    fun startAnimation(a: Animation, a1: Animation, a2: Animation, a3: Animation, a4: Animation, a5: Animation){
        binding.gameSayac.startAnimation(a)
        binding.gameAuthor.startAnimation(a1)
        binding.gameStylish.startAnimation(a2)
        binding.gameStylish1.startAnimation(a3)
        binding.gameStylish2.startAnimation(a4)
        binding.gameStylish3.startAnimation(a5)
    }
    fun enabledButton(enabled:Boolean){
        binding.gameStylish.isClickable=enabled
        binding.gameStylish1.isClickable=enabled
        binding.gameStylish2.isClickable=enabled
        binding.gameStylish3.isClickable=enabled
    }
    fun textViewBackground(){
        binding.gameStylish.setBackgroundResource(R.drawable.game_stylishs_shape)
        binding.gameStylish1.setBackgroundResource(R.drawable.game_stylishs_shape)
        binding.gameStylish2.setBackgroundResource(R.drawable.game_stylishs_shape)
        binding.gameStylish3.setBackgroundResource(R.drawable.game_stylishs_shape)
    }
    fun getQuestion(activity: GameActivity){
        val eserr =ArrayList<Eserler>()
        val question1= Random.nextInt(1302)
        val question2= Random.nextInt(1302)
        val question3= Random.nextInt(1302)
        val question4= Random.nextInt(1302)
        compain.db().collection("Yazar-Eser Listesi").addSnapshotListener { value, error ->
            if (error!=null){
                Toast.makeText(activity.applicationContext,error.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                if (value!=null){
                    eserr.clear()
                    for (degerler in value){
                        if (degerler.id.toInt()==question1 || degerler.id.toInt()==question2 ||degerler.id.toInt()==question3 || degerler.id.toInt()==question4) {
                            val yazar = degerler.get("Yazar Adı") as String
                            val eser = degerler.get("Eser Adı") as String
                            println(degerler.id)
                            eserr.add(Eserler(yazar, eser))
                        }
                    }
                    eserler.value=eserr
                }
            }
        }
    }
    fun authorControl(eserler:ArrayList<Eserler>):Boolean{
        if (eserler.size!=4){
            return false
        }else  if (eserler[0].yazarAdi.equals(eserler[1].yazarAdi) && eserler[0].yazarAdi.equals(eserler[2].yazarAdi) && eserler[0].yazarAdi.equals(eserler[3].yazarAdi)){
            return false
        }else if (eserler[1].yazarAdi.equals(eserler[2].yazarAdi) && eserler[1].yazarAdi.equals(eserler[3].yazarAdi)){
            return false
        }else if (eserler[2].yazarAdi.equals(eserler[3].yazarAdi)){
            return false
        }
        return true
    }
    fun selectedButton(random: Int){
        buttonBackground()
        if (random==0){
            binding.gameStylish.setBackgroundResource(R.drawable.selected_true_sahpe)
        }else if (random==1){
            binding.gameStylish1.setBackgroundResource(R.drawable.selected_true_sahpe)
        }else if (random==2){
            binding.gameStylish2.setBackgroundResource(R.drawable.selected_true_sahpe)
        }else if (random==3){
            binding.gameStylish3.setBackgroundResource(R.drawable.selected_true_sahpe)
        }
    }
    fun buttonBackground(){
        binding.gameStylish.setBackgroundResource(R.drawable.selected_false_shape)
        binding.gameStylish1.setBackgroundResource(R.drawable.selected_false_shape)
        binding.gameStylish2.setBackgroundResource(R.drawable.selected_false_shape)
        binding.gameStylish3.setBackgroundResource(R.drawable.selected_false_shape)
    }
    fun saveQuestion(activity: GameActivity,authorName: String,hashMap: HashMap<String, Any>) {
        compain.db().collection("${compain.auth().currentUser!!.email.toString()} Kaydedilenleri").get().addOnSuccessListener { valueSave ->
            if (valueSave.isEmpty) {
                saveDb(activity, hashMap)
            } else {
                saveQuestionControl(activity, authorName, hashMap)
            }
        }.addOnFailureListener {
            Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun saveQuestionControl(activity: GameActivity,authorName: String,hashMap: HashMap<String,Any>){
        var saveState=0
        compain.db().collection("${compain.auth().currentUser!!.email.toString()} Kaydedilenleri").get().addOnSuccessListener { state->
            val values =state.documents
            for (value in values){
                val dbAuthorName=value.get("Eser Adı") as String
                if (dbAuthorName.equals(authorName)){
                    Toast.makeText(activity.applicationContext,"Bu eseri zaten kayıt ettiniz!!!",Toast.LENGTH_SHORT).show()
                    saveState=1
                    break
                }
            }
            if (saveState==0){
                saveDb(activity,hashMap)
            }
        }.addOnFailureListener {
            Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun saveDb(activity: GameActivity,hashmap: HashMap<String, Any>){
        compain.db().collection("${compain.auth().currentUser!!.email.toString()} Kaydedilenleri").add(hashmap).addOnSuccessListener {
            Toast.makeText(activity.applicationContext,"Kaydedildi...",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(activity.applicationContext, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}