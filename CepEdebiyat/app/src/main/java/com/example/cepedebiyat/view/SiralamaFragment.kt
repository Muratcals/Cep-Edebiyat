package com.example.cepedebiyat.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cepedebiyat.R
import com.example.cepedebiyat.adapter.SiralamRecylerView
import com.example.cepedebiyat.databinding.FragmentSiralamaBinding
import com.example.cepedebiyat.model.Siralamalar
import com.example.cepedebiyat.unit.DownloadUrl
import com.example.cepedebiyat.viewModel.SiralamaViewModel

class SiralamaFragment : Fragment() {

    private lateinit var viewModel: SiralamaViewModel
    private lateinit var binding:FragmentSiralamaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSiralamaBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Sıralama Fragment")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Sıralamalar")
        viewModel = ViewModelProvider(this).get(SiralamaViewModel::class.java)
        viewModel.getData(view)
        viewModel.items.observe(viewLifecycleOwner) {
            println(it.size)
            val adapter = SiralamRecylerView(it as ArrayList<Siralamalar>)
            binding.siralamaRecycler.adapter=adapter
            binding.siralamaRecycler.layoutManager=LinearLayoutManager(requireContext())
            //firstArragment(it)
        }
        viewModel.progress.observe(viewLifecycleOwner) {
            if (it){
                binding.profilProgress.visibility=View.VISIBLE
                binding.siralamaRecycler.visibility=View.INVISIBLE
            }else{
                binding.profilProgress.visibility=View.INVISIBLE
                binding.siralamaRecycler.visibility=View.VISIBLE
            }
        }
    }

    /*
    fun firstArragment(items:ArrayList<Siralamalar>){
        if (items.isNotEmpty()){
            binding.firstName.text=items[0].authName
            binding.firstScore.text="${items[0].puan.toString()}pt"
            binding.firstImage.DownloadUrl(items[0].image)
            if (items[0].gender.equals("Erkek")) binding.firstImage.setImageResource(R.drawable.man) else binding.firstImage.setImageResource(R.drawable.woman)
        }
        if (items.size>1){
            binding.secondName.text=items[1].authName
            binding.secondScore.text="${items[1].puan.toString()}pt"
            binding.secondImage.DownloadUrl(items[1].image)
            if (items[1].gender.equals("Erkek")) binding.firstImage.setImageResource(R.drawable.man) else binding.firstImage.setImageResource(R.drawable.woman)
        }
        if (items.size>2){
            binding.thirdName.text=items[2].authName
            binding.thirdScore.text="${items[2].puan.toString()}pt"
            binding.thirdImage.DownloadUrl(items[2].image)
            if (items[2].gender.equals("Erkek")) binding.firstImage.setImageResource(R.drawable.man) else binding.firstImage.setImageResource(R.drawable.woman)
        }
    }

     */
}