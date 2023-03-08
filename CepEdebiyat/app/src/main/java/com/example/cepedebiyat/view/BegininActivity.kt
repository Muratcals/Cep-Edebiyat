package com.example.cepedebiyat.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cepedebiyat.MainActivity
import com.example.cepedebiyat.databinding.ActivityBegininBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BegininActivity : AppCompatActivity() {
    var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBegininBinding.inflate(layoutInflater)
        val bindingRoot = binding.root
        supportActionBar!!.hide()
        setContentView(bindingRoot)
        val sayac = Handler()
        val countDownTimer = object : CountDownTimer(4000, 290) {
            override fun onTick(millisUntilFinished: Long) {
                if (id == 0) {
                    sayac.postDelayed({
                        binding.a11.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
                if (id == 1) {
                    sayac.postDelayed({
                        binding.a10.visibility = View.VISIBLE
                        id++
                    }, 300)

                }
                if (id == 2) {
                    sayac.postDelayed({
                        binding.a9.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
                if (id == 3) {
                    sayac.postDelayed({
                        binding.a8.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
                if (id == 4) {
                    sayac.postDelayed({
                        binding.a7.visibility = View.VISIBLE
                        id++
                    }, 300)

                }
                if (id == 5) {
                    sayac.postDelayed({
                        binding.a6.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
                if (id == 6) {
                    sayac.postDelayed({
                        binding.a5.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
                if (id == 7) {
                    sayac.postDelayed({
                        binding.a4.visibility = View.VISIBLE
                        id++
                    }, 300)

                }
                if (id == 8) {
                    sayac.postDelayed({
                        binding.a3.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
                if (id == 9) {
                    sayac.postDelayed({
                        binding.a2.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
                if (id == 10) {
                    sayac.postDelayed({
                        binding.a1.visibility = View.VISIBLE
                        id++
                    }, 300)
                }
            }
            override fun onFinish() {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.P){
                    val connectivityManager=applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    if (connectivityManager.activeNetwork!=null){
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(applicationContext,"İnternet bağlantınızı kontrol ediniz",Toast.LENGTH_SHORT).show()
                        this@BegininActivity.finish()

                    }
                }
            }
        }
        countDownTimer.start()
    }
}