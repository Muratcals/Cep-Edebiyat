package com.example.cepedebiyat.view

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.example.cepedebiyat.MainActivity
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentRegisterBinding
import com.example.cepedebiyat.unit.compain
import com.example.cepedebiyat.viewModel.RegisterViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient


class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding:FragmentRegisterBinding
    private lateinit var name:String
    private lateinit var lastname:String
    private lateinit var authName:String
    private lateinit var password:String
    private lateinit var passwordReplay:String
    private lateinit var eMail:String
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentRegisterBinding.inflate(layoutInflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Register Fragment")
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Kayıt  Ol")
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        arguments.let {
            observerItem()
            val state =it!!.getString("state")
            state?.let {
                if(it.equals("Google")){
                    mGoogleSignInClient=GoogleSignIn.getClient(requireActivity(),compain.gso)
                    val googleSignInAccount=GoogleSignIn.getLastSignedInAccount(requireContext())
                    if (googleSignInAccount!=null){
                        getAuthInformation(googleSignInAccount)
                    }
                    binding.registerPassword.visibility=View.GONE
                    binding.registerPasswordReplay.visibility=View.GONE
                    binding.registerPasswordError2.visibility=View.GONE
                    binding.registerPasswordError1.visibility=View.GONE
                    binding.registerEmail.isEnabled=false
                }
            }
            binding.registerPassword.addTextChangedListener {
                viewModel.passwordControl(binding.registerPassword.text.toString())
                viewModel.replayPasswordControl(binding.registerPassword.text.toString(),binding.registerPasswordReplay.text.toString())
            }
            binding.registerPasswordReplay.addTextChangedListener {
                viewModel.replayPasswordControl(binding.registerPassword.text.toString(),binding.registerPasswordReplay.text.toString())
            }
            binding.registerButton.setOnClickListener {
                if (emptyItemControl()){
                    observerItem()
                    compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("Auth Name",binding.registerAuthName.text.toString()).get().addOnSuccessListener {
                        if (it.isEmpty){
                            if (state!!.equals("Register")){
                                if (binding.registerPassword.isVisible &&  binding.registerPassword.text.toString().isNotEmpty() && binding.registerPasswordReplay.text.toString().isNotEmpty()){
                                    viewModel.registerAuth(requireActivity(),state,view,binding)
                                }else{
                                    Toast.makeText(requireContext(),"Lütfen şifrenizi giriniz",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                viewModel.registerAuth(requireActivity(),state,view,binding)
                            }
                        }else{
                            Toast.makeText(view.context,"Bu kullanıcı adı alınmıştır",Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    val message =Toast.makeText( requireContext(),"Lütfen bütün alanları eksiksiz ve hatasız doldurunuz", Toast.LENGTH_SHORT)
                    message.show()
                }
            }
        }

    }
    fun observerItem(){
        viewModel.error1.observe(viewLifecycleOwner, Observer{
            if (it){
                binding.registerPasswordError1.visibility=View.VISIBLE
            }else{
                binding.registerPasswordError1.visibility=View.INVISIBLE
            }
        })
        viewModel.error2.observe(viewLifecycleOwner,Observer{
            if (it){
                binding.registerPasswordError2.visibility=View.VISIBLE
            }else{
                binding.registerPasswordError2.visibility=View.INVISIBLE
            }
        })
        viewModel.progressBar.observe(viewLifecycleOwner,Observer{
            if (it){
                isEnabledItem(false)
                binding.registerProgress.visibility=View.VISIBLE
                binding.loginLinear.alpha=0.4F
            }else{
                isEnabledItem(true)
                binding.registerProgress.visibility=View.INVISIBLE
                binding.loginLinear.alpha=1F
            }
        })
    }

    fun emptyItemControl() :Boolean{
        name=binding.registerName.text.toString()
        lastname=binding.registerLastName.text.toString()
        authName=binding.registerAuthName.text.toString()
        password=binding.registerPassword.text.toString()
        passwordReplay=binding.registerPasswordReplay.text.toString()
        eMail=binding.registerEmail.text.toString()
        if ((name.isNotEmpty() && lastname.isNotEmpty() && authName.isNotEmpty() &&
                    eMail.isNotEmpty()) && (binding.registerKadin.isChecked || binding.registerErkek.isChecked)
            && binding.registerPasswordError1.isVisible==false && binding.registerPasswordError2.isVisible==false){
            return true
        }else{
            return false
        }
    }
    fun isEnabledItem(duraction:Boolean){
        binding.registerButton.isEnabled=duraction
        binding.registerPassword.isEnabled=duraction
        binding.registerPasswordReplay.isEnabled=duraction
        binding.registerName.isEnabled=duraction
        binding.registerLastName.isEnabled=duraction
        binding.registerAuthName.isEnabled=duraction
        binding.registerButton.isEnabled=duraction
        binding.registerEmail.isEnabled=duraction
    }
    //Kullanıcı Bilgileri
    fun getAuthInformation(googleSignInAccount:GoogleSignInAccount){
        val eMail=googleSignInAccount.email
        val givenName=googleSignInAccount.givenName
        val familyName=googleSignInAccount.familyName
        binding.registerEmail.setText(eMail)
        binding.registerName.setText(givenName)
        binding.registerLastName.setText(familyName)
    }
}