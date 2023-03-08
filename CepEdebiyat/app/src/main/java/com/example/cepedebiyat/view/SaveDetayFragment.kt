package com.example.cepedebiyat.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentSaveDetayBinding
import com.example.cepedebiyat.viewModel.SaveDetayViewModel

class SaveDetayFragment : Fragment() {
    private lateinit var viewModel: SaveDetayViewModel
    private lateinit var binding:FragmentSaveDetayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentSaveDetayBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Save Detay Fragment")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(SaveDetayViewModel::class.java)
        arguments.let {
            val id =it!!.getString("id")
            val state =it.getBoolean("state")
            viewModel.getQuestionDetail(id!!)
            viewModel.item.observe(viewLifecycleOwner){
                binding.saveStylish.text=it.item1
                binding.saveStylish1.text=it.item2
                binding.saveStylish2.text=it.item3
                binding.saveStylish3.text=it.item4
                binding.saveAuthor.text=it.eserAdi
                stylishBackground(it.selectedItem,it.eserAdi,state)
            }
            viewModel.progress.observe(viewLifecycleOwner){
                if (it){
                    binding.saveProgress.visibility=View.VISIBLE
                }else{
                    binding.saveProgress.visibility=View.INVISIBLE
                }
            }
            binding.saveDelete.setOnClickListener {
                viewModel.questionDelete(view,id)
                requireActivity().supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            }
        }
    }

    fun stylishBackground(selectedButonText:String,workName:String,state:Boolean){
        if (state){
            if (binding.saveStylish.text.toString().equals(selectedButonText)) stylishBackgroundControl(0)
            if (binding.saveStylish1.text.toString().equals(selectedButonText)) stylishBackgroundControl(1)
            if (binding.saveStylish2.text.toString().equals(selectedButonText)) stylishBackgroundControl(2)
            if (binding.saveStylish3.text.toString().equals(selectedButonText)) stylishBackgroundControl(3)
        }else{
            viewModel.questionStateControl(selectedButonText,binding,workName)
        }
    }

    fun stylishBackgroundControl(buttonId:Int){
        if (buttonId==0) binding.saveStylish.setBackgroundResource(R.drawable.selected_true_sahpe)
        else   binding.saveStylish.setBackgroundResource(R.drawable.selected_false_shape)
        if (buttonId==1) binding.saveStylish1.setBackgroundResource(R.drawable.selected_true_sahpe)
        else  binding.saveStylish1.setBackgroundResource(R.drawable.selected_false_shape)
        if (buttonId==2) binding.saveStylish2.setBackgroundResource(R.drawable.selected_true_sahpe)
        else  binding.saveStylish2.setBackgroundResource(R.drawable.selected_false_shape)
        if (buttonId==3) binding.saveStylish3.setBackgroundResource(R.drawable.selected_true_sahpe)
        else  binding.saveStylish3.setBackgroundResource(R.drawable.selected_false_shape)

    }
}