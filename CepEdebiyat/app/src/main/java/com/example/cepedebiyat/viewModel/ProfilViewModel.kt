package com.example.cepedebiyat.viewModel

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentProfilBinding
import com.example.cepedebiyat.model.ProfilInformation
import com.example.cepedebiyat.unit.DownloadUrl
import com.example.cepedebiyat.unit.compain
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class ProfilViewModel : ViewModel() {
    val deleteAccountError =MutableLiveData<Boolean>()
    val profilProgress=MutableLiveData<Boolean>()
    val error=MutableLiveData<Boolean>()
    val information=MutableLiveData<List<ProfilInformation>>()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    fun authLogout():Boolean{
        compain.auth().currentUser?.let {
            compain.auth().signOut()
            return true
        }
        return false
    }

    fun deleteTrypho(view: View){
        compain.db().collection("Sıralamalar").whereEqualTo("E-Mail",compain.auth().currentUser!!.email).get().addOnSuccessListener {
            val id =it.documents[0].id
            compain.db().collection("Sıralamalar").document(id).delete().addOnSuccessListener {
                compain.db().collection("${compain.auth().currentUser!!.email.toString()} Kaydedilenleri").document().delete().addOnSuccessListener {
                    compain.auth().currentUser!!.delete().addOnSuccessListener {
                        if (mGoogleSignInClient!=null){
                            mGoogleSignInClient.signOut()
                        }
                        Toast.makeText(view.context,"Kullanıcı Başarıyla silinmiştir",Toast.LENGTH_SHORT).show()
                        compain.auth().signOut()
                        deleteAccountError.value=false
                        profilProgress.value=false
                    }
                }
            }
        }
    }
    fun deleteAccount(view:View){
        mGoogleSignInClient=GoogleSignIn.getClient(view.context,compain.gso)
        profilProgress.value=true
        compain.auth().currentUser?.let { user->
            val alert =alertDeleteAccount(view)
            alert.setPositiveButton("Onaylıyorum",DialogInterface.OnClickListener { dialog, which ->
                compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",user.email).get().addOnSuccessListener { value->
                    val id =value.documents[0].id
                    val authName=value.documents[0].get("Auth Name") as String
                    compain.db().collection("Kullanıcı Bilgileri").document(id).delete().addOnCompleteListener { authDeleteState->
                        if (authDeleteState.isSuccessful){
                           deleteTrypho(view)
                        }else{
                            Toast.makeText(view.context,authDeleteState.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
                            profilProgress.value=false
                        }
                    }
                }
            })
            alert.setNegativeButton("Iptal et",DialogInterface.OnClickListener { dialog, which ->
                profilProgress.value=false
            })
            alert.show()
        }

    }

    fun saveImageDatabase(activity: Activity,imageUri: Uri){
        profilProgress.value=true
        compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",compain.auth().currentUser!!.email).get().addOnSuccessListener {
            val valueId =it.documents[0].id
            val hashmap= hashMapOf<String,Any>()
            hashmap.put("Profil Image",imageUri.toString())
            compain.db().collection("Kullanıcı Bilgileri").document(valueId).update(hashmap).addOnSuccessListener {
                Toast.makeText(activity.applicationContext,"Profil resmi değiştirildi",Toast.LENGTH_SHORT).show()
                profilProgress.value=false
                getAuthInformation()
            }
        }
    }
    fun updateProfilImage(binding: FragmentProfilBinding){
        compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",compain.auth().currentUser!!.email).get().addOnSuccessListener {
            val value =it.documents
            val profilImageUrl =value[0].get("Profil Image") as String
            val cinsiyet =value[0].get("Cinsiyet") as String
            if (profilImageUrl==""){
                if (cinsiyet.equals("Erkek")) binding.profilImage.setImageResource(R.drawable.man) else binding.profilImage.setImageResource(R.drawable.woman)
            }else{
                binding.profilImage.DownloadUrl(profilImageUrl)
            }
        }
    }
    fun alertDeleteAccount(view:View):AlertDialog.Builder{
        val alert=AlertDialog.Builder(view.context)
        alert.setMessage("Eğer kullanıcı silinirse bir daha geri getirelemez." +
                "Onaylıyor musunuz ?")
        alert.setTitle("Kullanıcı Silme İşlemi")
        return alert
    }

    fun getAuthInformation(){
        profilProgress.value=true
        compain.db().collection("Sıralamalar").whereEqualTo("E-Mail",compain.auth().currentUser!!.email).get().addOnSuccessListener { value->
            if (value!=null){
                val items =value.documents
                val pr =ArrayList<ProfilInformation>()
                for (item in items){
                    val authName=item.get("Auth Name") as String
                    val score=item.get("Puan") as Number
                    val dogruSayisi =item.get("Dogru Sayisi") as Number
                    val toplamSoru=item.get("Toplam Soru") as Number
                    pr.add(ProfilInformation(authName,dogruSayisi,toplamSoru,score))
                }
                information.value=pr
            }
            profilProgress.value=false
        }
    }
    fun shareApp(view:View){
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.setType("Uyguluma Paylaş")
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT,"https://drive.google.com/drive/folders/1vqbukir_NIHdY4vWzvBeqKov_qINCjtt?usp=share_link")
        startActivity(view.context,intent,null)
    }
}