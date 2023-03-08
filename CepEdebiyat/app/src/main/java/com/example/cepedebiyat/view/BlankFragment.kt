package com.example.cepedebiyat.view

import android.app.ActionBar.LayoutParams
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.marginStart
import com.example.cepedebiyat.databinding.FragmentBlankBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firestore.v1.StructuredAggregationQuery.Aggregation.Count
import java.io.File
import java.io.FileInputStream
import java.net.URI
import kotlin.random.Random
import kotlin.random.nextInt

class BlankFragment : Fragment() {

    private lateinit var binding:FragmentBlankBinding
    private lateinit var db :FirebaseFirestore
    val list2 = arrayListOf<String>("Şahdamar","","","","","","","","","","","","","","","","","","","")
    var x=0
    var y =1229
    var z=0
    var random = Random.nextInt(4)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentBlankBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
    fun sayac (list:ArrayList<String>,yazar:ArrayList<String>) :CountDownTimer{
        return object  :CountDownTimer(list.size.toLong()*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                if (x<list.size){
                    db= FirebaseFirestore.getInstance()
                    val hashmap = hashMapOf<String,Any>()
                    hashmap.put("Yazar Adı",yazar[z])
                    hashmap.put("Eser Adı",list[x])
                    db.collection("Yazar-Eser Listesi").document(y.toString()).set(hashmap).addOnSuccessListener {
                        Toast.makeText(requireContext(),"${yazar[z]} ${list[x]}",Toast.LENGTH_SHORT).show()
                        if (x==6 ||x ==20 || x==42 || x==43 || x==44 || x==46 || x==47 || x==55 ||x==58 ||x==64 ||x==67){
                            z++
                        }
                        x++
                        y++
                    }
                }

            }
            override fun onFinish() {
                Toast.makeText(requireContext(),"bitti",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getData(){
        db= FirebaseFirestore.getInstance()
        db.collection("Yazar-Eser Listesi").addSnapshotListener { value, error ->
            if (value!=null){
                val degerler =value.documents
                for (deger in degerler){
                    val yazar =deger.get("Yazar Adı") as String
                    val eser =deger.get("Eser Adı") as String
                    println("""Yazar"": ""${yazar}"",""Eser"": ""${eser}""")
                }
            }
        }
    }

    fun dosyaOku(){
        var icerik =""
        val uri ="C:\\Users\\murat\\Desktop\\deneme.txt"
        val urii =URI.create(uri)
        val dosyaDir= File(urii)

        icerik =FileInputStream(dosyaDir).bufferedReader().use {
            it.readLine()
        }
        println(icerik)
    }
}