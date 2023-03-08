package com.example.cepedebiyat.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cepedebiyat.R
import com.example.cepedebiyat.adapter.SaveAdapter
import com.example.cepedebiyat.databinding.FragmentSaveBinding
import com.example.cepedebiyat.model.SaveModel
import com.example.cepedebiyat.viewModel.SaveViewModel

class SaveFragment : Fragment() {
    private lateinit var viewModel: SaveViewModel
    private lateinit var binding :FragmentSaveBinding
    private lateinit var adapter:SaveAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding=FragmentSaveBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Save Fragment")
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Kaydedilenler")
        if (requireActivity().actionBar != null) {
            requireActivity().actionBar!!.setTitle(("title"));
            requireActivity().actionBar!!.setDisplayHomeAsUpEnabled(true);
        }
        viewModel = ViewModelProvider(this).get(SaveViewModel::class.java)
        viewModel.getAuthorName()
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.saveRecycler.visibility=View.INVISIBLE
                binding.saveProgress.visibility=View.VISIBLE
            }else{
                binding.saveRecycler.visibility=View.VISIBLE
                binding.saveProgress.visibility=View.INVISIBLE
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            if (it){
                binding.saveRecycler.visibility=View.INVISIBLE
                binding.saveDetayHata.visibility=View.VISIBLE
            }else{
                binding.saveRecycler.visibility=View.VISIBLE
                binding.saveDetayHata.visibility=View.INVISIBLE
            }
        }
        viewModel.authorNameList.observe(viewLifecycleOwner,Observer{
            adapter= SaveAdapter(view,it as ArrayList<SaveModel>)
            adapter.let {
                binding.saveRecycler.adapter=adapter
                binding.saveRecycler.layoutManager=GridLayoutManager(requireContext(),2)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}