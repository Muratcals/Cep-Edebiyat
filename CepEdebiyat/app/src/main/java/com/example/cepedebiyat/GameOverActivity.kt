package com.example.cepedebiyat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.cepedebiyat.databinding.ActivityGameOverBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GameOverActivity : AppCompatActivity() {
    private lateinit var binding:ActivityGameOverBinding
    private lateinit var db:FirebaseFirestore
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGameOverBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar!!.hide()
        process()
        binding.gameoverHome.setOnClickListener {
            val action =Intent(applicationContext,MainActivity::class.java)
            startActivity(action)
            this.finish()
        }
        binding.gameOver.setOnClickListener {
            this.finish()
        }
        binding.gameOverRestart.setOnClickListener {
            val action =Intent(applicationContext,GameActivity::class.java)
            startActivity(action)
            this.finish()
        }
    }
    //Sayfada gözükecek işlemleri yaparız.
    fun process (){
        val cozulenSoru =intent.getDoubleExtra("cozulenSoru",0.0)
        val dogruSayisi =intent.getDoubleExtra("dogruSayisi",0.0)
        val yanlisSayisi=intent.getDoubleExtra("yanlisSayisi",0.0)
        val dogrulukOrani=Math.floor((dogruSayisi/cozulenSoru)*100)
        val puan =(dogruSayisi.toInt()*2)-(yanlisSayisi.toInt()*1)
        binding.dogruSayisi.text=dogruSayisi.toInt().toString()
        binding.yanlisSayisi.text=yanlisSayisi.toInt().toString()
        if (cozulenSoru!=0.0){
            binding.dogrulukOrani.text="${dogrulukOrani}%"
        }else{
            binding.dogrulukOrani.text="0%"
        }
        binding.soruSayisi.text=cozulenSoru.toInt().toString()
        binding.skor.text="Skorun ${puan} pts"
        getScore(puan,cozulenSoru,dogruSayisi)
    }
    //Firebaseden skorları çekiyoruz.
    fun getScore(puan:Number,soruSayisi:Number,dogruSayisi:Number){
        db=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        db.collection("Sıralamalar").whereEqualTo("E-Mail",auth.currentUser!!.email).get().addOnSuccessListener { value->
            val document= value.documents[0]
            val id =document.id
            val puann =document.get("Puan") as Number
            val toplamSoruSayisi=document.get("Toplam Soru") as Number
            val toplamDogruSayisi =document.get("Dogru Sayisi") as Number
            val hashmap= hashMapOf<String,Any>()
            hashmap.put("Puan",puann.toInt()+puan.toInt())
            hashmap.put("Toplam Soru",soruSayisi.toInt()+toplamSoruSayisi.toInt())
            hashmap.put("Dogru Sayisi",dogruSayisi.toInt()+toplamDogruSayisi.toInt())
            db.collection("Sıralamalar").document(id).update(hashmap).addOnCompleteListener { state->

            }.addOnFailureListener { error->
                Toast.makeText(applicationContext,error.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }
}