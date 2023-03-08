package com.example.cepedebiyat

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.cepedebiyat.databinding.ActivityMainBinding
import com.example.cepedebiyat.unit.DownloadUrl
import com.example.cepedebiyat.unit.compain
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import java.sql.Connection


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var headerName:TextView
    private lateinit var headerMail:TextView
    private lateinit var headerImage:CircleImageView
    private lateinit var connectionNetwork :ConnectivityManager
    private val auth =FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toggle =ActionBarDrawerToggle(this,binding.Drawer,R.string.open,R.string.close)
        binding.Drawer.addDrawerListener(toggle)
        toggle.syncState()
        if (auth.currentUser!=null){
            getAuthInformation()
        }
        val connection =applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connection.activeNetwork==null){

        }
        //header tasarımı ekleme
        val iconView=LayoutInflater.from(applicationContext).inflate(R.layout.menu_header,null,false)
        headerName =iconView.findViewById(R.id.headerTitle)
        headerMail =iconView.findViewById(R.id.headerEmail)
        headerImage=iconView.findViewById(R.id.headerImage)
        binding.gameNavigation.addHeaderView(iconView)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val navController =supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        binding.gameNavigation.setupWithNavController(navController.navController)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //yandan açılır menu bilgilerini getirme
    fun getAuthInformation(){
        compain.auth().currentUser?.let{
            compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",auth.currentUser!!.email.toString()).addSnapshotListener { value, error ->
                if (error!=null){
                    Toast.makeText(applicationContext,error.localizedMessage,Toast.LENGTH_SHORT).show()
                }else{
                    if (value!=null){
                        val authInformations =value.documents
                        for (x in authInformations){
                            val name =x.get("Name") as String
                            val lastName =x.get("Last Name") as String
                            val profilImage=x.get("Profil Image") as String
                            val cinsiyet =x.get("Cinsiyet") as String
                            val eMail =x.get("E-Mail") as String
                            headerMail.text=eMail
                            headerName.text="${name} ${lastName}"
                            if (profilImage==""){
                                if (cinsiyet.equals("Erkek")) headerImage.setImageResource(R.drawable.man) else headerImage.setImageResource(R.drawable.woman)
                            }else{
                                headerImage.DownloadUrl(profilImage) }
                        }
                    }else{
                        compain.auth().signOut()
                        val action =Intent(applicationContext,MainActivity::class.java)
                        startActivity(action)
                        this.finish()
                    }
                }
            }
        }
    }

    /*
    override fun onBackPressed() {
        if (cikis==0){
            showCustomPopupMenu()
            cikis++
        }else{
            this.finish()
        }
    }

     */

    override fun onStart() {
        super.onStart()
        if (compain.auth().currentUser==null){
            if (supportActionBar!!.isShowing){
                supportActionBar!!.hide()
            }
        }else{
            if (!supportActionBar!!.isShowing)
            supportActionBar!!.show()
        }
    }
}