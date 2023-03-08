package com.example.cepedebiyat.adapter

import android.app.ActionBar.LayoutParams
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cepedebiyat.R
import com.example.cepedebiyat.model.Siralamalar
import com.example.cepedebiyat.unit.DownloadUrl
import de.hdodenhof.circleimageview.CircleImageView

class SiralamRecylerView(val item: ArrayList<Siralamalar>):RecyclerView.Adapter<SiralamRecylerView.SiralamaVH>() {
    class SiralamaVH(view:View):RecyclerView.ViewHolder(view)  {
        //val id :TextView
        val name :TextView
        val score :TextView
        val image :CircleImageView
        //val text1:TextView
        //val text2:TextView
        val linearLayout :LinearLayout
        init {
            //id=view.findViewById(R.id.trophyId)
            name=view.findViewById(R.id.trophyName)
            score=view.findViewById(R.id.trophyScore)
            image=view.findViewById(R.id.trophyImage)
            linearLayout=view.findViewById(R.id.trophyLinear)
            //text1 =view.findViewById(R.id.textView3)
            //text2 =view.findViewById(R.id.textView4)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SiralamaVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.trophy_reycler,parent,false)
        return SiralamaVH(view)
    }
    override fun onBindViewHolder(holder: SiralamaVH, position: Int) {
        println(item[position].puan.toInt())
        holder.name.text=item[position].authName
        holder.score.text="${item[position].puan.toString()} pt"
        val params =holder.linearLayout.layoutParams as ViewGroup.MarginLayoutParams
        params.width=item[position].puan.toInt()*2
        if (item[position].puan.toInt()>=500){
            params.width=LayoutParams.MATCH_PARENT
        }
        if (item[position].image==""){
            println("başarılı değil")
            if (item[position].gender.equals("Erkek")){
                holder.image.setImageResource(R.drawable.man)
            }else{
                holder.image.setImageResource(R.drawable.woman)
            }
        }else{
            println(item[position].image)
            holder.image.DownloadUrl(item[position].image)
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }
}