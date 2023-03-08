package com.example.cepedebiyat.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.cepedebiyat.R
import com.example.cepedebiyat.unit.DownloadUrl
import com.example.cepedebiyat.unit.compain
import com.example.cepedebiyat.view.AvatarSelectedFragment
import de.hdodenhof.circleimageview.CircleImageView

class AvatarRecycler(val view:View,val avatars :ArrayList<String>) :RecyclerView.Adapter<AvatarRecycler.AvatarVh>(){
    class AvatarVh(view:View) :RecyclerView.ViewHolder(view){
        val image:CircleImageView
        init {
            image=view.findViewById(R.id.avatarImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarVh {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.avatar_recycler,parent,false)
        return AvatarVh(view)
    }

    override fun onBindViewHolder(holder: AvatarVh, position: Int) {
        holder.image.DownloadUrl(avatars[position])
        holder.image.setOnClickListener {
            message(avatars[position])
        }
    }

    override fun getItemCount(): Int {
        return avatars.size
    }

    fun message(url :String){
        val uyari = AlertDialog.Builder(view.context)
        uyari.setMessage("Avatarınızı değiştirmek istediğinize emin misiniz")
        uyari.setPositiveButton("Evet", DialogInterface.OnClickListener { dialog, which ->
            compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail", compain.auth().currentUser!!.email).get().addOnSuccessListener {
                val id = it.documents[0].id
                compain.db().collection("Kullanıcı Bilgileri").document(id).update("Profil Image",url).addOnSuccessListener {
                    compain.db().collection("Sıralamalar").whereEqualTo("E-Mail",compain.auth().currentUser!!.email.toString()).get().addOnSuccessListener { value->
                        val siralamaId =value.documents[0].id
                        compain.db().collection("Sıralamalar").document(siralamaId).update("Profil Image",url).addOnSuccessListener {
                            Toast.makeText(view.context,"Avatarınız güncellendi.",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            view.findNavController().navigate(R.id.action_avatarSelectedFragment_to_profilFragment2)
        })
        uyari.setNegativeButton("Hayır", DialogInterface.OnClickListener { dialog, which ->

        })
        uyari.show()
    }

}