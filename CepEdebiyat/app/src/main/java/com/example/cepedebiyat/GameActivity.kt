package com.example.cepedebiyat

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.example.cepedebiyat.databinding.ActivityGameBinding
import com.example.cepedebiyat.viewModel.GameStartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class GameActivity : AppCompatActivity(){

    private lateinit var viewModel: GameStartViewModel
    private lateinit var binding: ActivityGameBinding
    private  lateinit var sayac : CountDownTimer
    private lateinit var selectedButtonText :String
    var cozulenSoru=0
    var dogruSayisi=0
    var yanlisSayisi=0
    var random =Random.nextInt(4)
    var questionAuthor:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel= GameStartViewModel(binding)
        viewModel.getQuestion(this)
        supportActionBar!!.hide()
        questionControl(random)
        val anim= AnimationUtils.loadAnimation(applicationContext,R.anim.game_animation1)
        val anim1= AnimationUtils.loadAnimation(applicationContext,R.anim.game_animation2)
        val anim2= AnimationUtils.loadAnimation(applicationContext,R.anim.game_animation3)
        val anim3= AnimationUtils.loadAnimation(applicationContext,R.anim.game_animation4)
        val anim4= AnimationUtils.loadAnimation(applicationContext,R.anim.game_animation5)
        val anim5= AnimationUtils.loadAnimation(applicationContext,R.anim.game_animation6)
        val anim6=AnimationUtils.loadAnimation(applicationContext,R.anim.game_animation7)
        random =Random.nextInt(4)
        sayac=viewModel.startSayac(random,anim6)
        sayac.start()
        viewModel.startAnimation(anim,anim1,anim2,anim3,anim4,anim5)
        binding.gameSayac.playAnimation()
        binding.gameStylish.setOnClickListener {
            selectedButtonControl(binding.gameStylish.text.toString(),0)
        }
        binding.gameStylish1.setOnClickListener {
            selectedButtonControl(binding.gameStylish1.text.toString(),1)
        }
        binding.gameStylish2.setOnClickListener {
            selectedButtonControl(binding.gameStylish2.text.toString(),2)
        }
        binding.gameStylish3.setOnClickListener {
            selectedButtonControl(binding.gameStylish3.text.toString(),3)
        }
        binding.nextQuestion.setOnClickListener {
            random=Random.nextInt(4)
            viewModel.enabledButton(true)
            viewModel.textViewBackground()
            binding.nextQuestion.visibility= View.INVISIBLE
            viewModel.getQuestion(this)
            questionControl(random)
            binding.gameSayac.playAnimation()
            viewModel.startAnimation(anim,anim1,anim2,anim3,anim4,anim5)
            sayac=viewModel.startSayac(random,anim6)
            sayac.start()
        }
        binding.gameBack.setOnClickListener {
            alert().show()
        }
        binding.gameSave.setOnClickListener {
            questionAuthor?.let {
                val hashmap =hashmapSave()
                viewModel.saveQuestion(this,it,hashmap)
            }
        }
    }
    override fun onPause() {
        super.onPause()
        binding.gameSayac.cancelAnimation()
        sayac.onFinish()
    }
    override fun onDestroy() {
        sayac.cancel()
        super.onDestroy()
    }

    fun hashmapSave():HashMap<String,Any>{
        val hashmap = hashMapOf<String, Any>()
        hashmap.put("Eser Adı", questionAuthor.toString())
        hashmap.put("Secilen Şık Text",selectedButtonText)
        hashmap.put("Şık1",binding.gameStylish.text.toString())
        hashmap.put("Şık2",binding.gameStylish1.text.toString())
        hashmap.put("Şık3",binding.gameStylish2.text.toString())
        hashmap.put("Şık4",binding.gameStylish3.text.toString())
        return hashmap
    }

    fun selectedButtonControl(selectedButton:String,buttonId:Int){
        if (random==buttonId){
            dogruSayisi+=1
        }else{
            yanlisSayisi+=1
        }
        selectedButtonText=selectedButton
        cozulenSoru+=1
        binding.gameSave.visibility=View.VISIBLE
        binding.gameSave.isClickable=true
        sayac.cancel()
        sayac.onFinish()
    }
    fun questionControl(random:Int){
        viewModel.eserler.observe(this, Observer {
            if (it.isEmpty()){
                println("denemee")
                viewModel.getQuestion(this)
            }else{
                println("deneme2")
                val value =viewModel.authorControl(it)
                if (value){
                    binding.gameStylish.text=it[0].yazarAdi
                    binding.gameStylish1.text=it[1].yazarAdi
                    binding.gameStylish2.text=it[2].yazarAdi
                    binding.gameStylish3.text=it[3].yazarAdi
                    binding.gameAuthor.text=it[random].eserAdi
                    questionAuthor=it[random].eserAdi
                }else{
                    viewModel.getQuestion(this)
                }
            }
        })
    }

    fun alert():AlertDialog.Builder{
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Çıkış Uyarısı")
        dialog.setMessage("Çıkış yapmak istediğinize emin misiniz ?")
        dialog.setPositiveButton("Menüye Dön",DialogInterface.OnClickListener { dialog, which ->
            val intent =Intent(applicationContext,GameOverActivity::class.java)
            intent.putExtra("cozulenSoru",cozulenSoru.toDouble())
            intent.putExtra("dogruSayisi",dogruSayisi.toDouble())
            intent.putExtra("yanlisSayisi",yanlisSayisi.toDouble())
            startActivity(intent)
            this.finish()
        })
        dialog.setNegativeButton("Devam et",DialogInterface.OnClickListener { dialog, which ->

        })
        return dialog
    }
    override fun onBackPressed() {
        alert().show()
    }
}