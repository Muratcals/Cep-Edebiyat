package com.example.cepedebiyat.adapter

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.cepedebiyat.R
import com.example.cepedebiyat.model.SaveModel

class SaveAdapter(val view: View,val list :ArrayList<SaveModel>):RecyclerView.Adapter<SaveAdapter.SaveVH>() {
    class SaveVH(view:View):RecyclerView.ViewHolder(view) {
        val text =view.findViewById<TextView>(R.id.saveItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaveVH {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.save_item_layout,parent,false)
        return SaveVH(view)
    }

    override fun onBindViewHolder(holder: SaveVH, position: Int) {
        if (list[position].state) holder.text.setBackgroundResource(R.drawable.selected_true_sahpe)
        else holder.text.setBackgroundResource(R.drawable.selected_false_shape)
        holder.text.text=list[position].authorName
        holder.text.setOnClickListener {
            val bundle = bundleOf("id" to list[position].id,"state" to list[position].state)
            view.findNavController().navigate(R.id.action_saveFragment_to_saveDetayFragment,bundle)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}