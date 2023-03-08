package com.example.cepedebiyat.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cepedebiyat.adapter.AvatarRecycler
import com.example.cepedebiyat.databinding.FragmentAvatarSelectedBinding
import com.example.cepedebiyat.viewModel.AvatarSelectedViewModel

class AvatarSelectedFragment : Fragment() {

    private lateinit var binding :FragmentAvatarSelectedBinding
    private lateinit var viewModel: AvatarSelectedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentAvatarSelectedBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Avatar Selected Fragment")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AvatarSelectedViewModel::class.java)
        viewModel.getAvatar()
        requireActivity().setTitle("Avatar Değiştir")
        viewModel.progress.observe(viewLifecycleOwner){
            if (it){
                binding.avatarProgress.visibility=View.VISIBLE
                binding.recyclerView.visibility=View.INVISIBLE
            }else{
                binding.avatarProgress.visibility=View.INVISIBLE
                binding.recyclerView.visibility=View.VISIBLE
            }
        }
        viewModel.error.observe(viewLifecycleOwner){
            if (it){
                binding.recyclerView.visibility=View.INVISIBLE
                binding.error.visibility=View.VISIBLE
            }else{
                binding.recyclerView.visibility=View.VISIBLE
                binding.error.visibility=View.INVISIBLE
            }
        }
        viewModel.avatars.observe(viewLifecycleOwner){
            val adapter =AvatarRecycler(view,it as ArrayList<String>)
            binding.recyclerView.layoutManager=GridLayoutManager(requireContext(),4)
            binding.recyclerView.adapter=adapter
        }
    }
}