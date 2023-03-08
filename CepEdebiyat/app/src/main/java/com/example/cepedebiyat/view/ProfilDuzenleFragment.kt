package com.example.cepedebiyat.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentProfilDuzenleBinding
import com.example.cepedebiyat.viewModel.ProfilDuzenleViewModel

class ProfilDuzenleFragment : Fragment() {

    private lateinit var viewModel: ProfilDuzenleViewModel
    private lateinit var binding :FragmentProfilDuzenleBinding
    private lateinit var authName:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfilDuzenleBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Profil Update Fragment")
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Profili Düzenle")
        viewModel = ViewModelProvider(this).get(ProfilDuzenleViewModel::class.java)
        viewModel.getProfilInformation()
        observerItem()
        binding.editButton.setOnClickListener {
            if (binding.editName.text.isNotEmpty() && binding.editAuthName.text.isNotEmpty() &&
                (binding.editKadin.isChecked || binding.editErkek.isChecked) &&
                    binding.editEmail.text.isNotEmpty() && binding.editLastName.text.isNotEmpty()){
                viewModel.updateInformation(authName,binding,view)
                this.onDestroy()
            }else{
                Toast.makeText(requireContext(),"Lütfen hiç bir alanı boş geçmeyiniz",Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun observerItem(){
        viewModel.progress.observe(viewLifecycleOwner, Observer { state->
            if (state){
                binding.editLinear.alpha=0.4F
                binding.editProgress.visibility=View.VISIBLE
            }else{
                binding.editLinear.alpha=1F
                binding.editProgress.visibility=View.GONE
            }
        })
        viewModel.information.observe(viewLifecycleOwner,Observer{ auth->
            authName=auth.authName
            binding.editName.setText(auth.name)
            binding.editLastName.setText(auth.lastName)
            binding.editEmail.setText(auth.eMail)
            binding.editAuthName.setText(auth.authName)
            if (auth.cinsiyet.equals("Erkek")){
                binding.editErkek.isChecked=true
            }else{
                binding.editKadin.isChecked=true
            }
        })
    }
}