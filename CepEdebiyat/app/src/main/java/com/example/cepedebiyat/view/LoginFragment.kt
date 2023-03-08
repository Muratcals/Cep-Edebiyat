package com.example.cepedebiyat.view


import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cepedebiyat.MainActivity
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentBlankBinding
import com.example.cepedebiyat.databinding.FragmentLoginBinding
import com.example.cepedebiyat.unit.compain
import com.example.cepedebiyat.viewModel.LoginViewModel
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.SignInButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding :FragmentLoginBinding
    private lateinit var mGoogleSignInClient:GoogleSignInClient
    val eMail:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().setTitle("Giriş Yap")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        mGoogleSignInClient=GoogleSignIn.getClient(requireActivity(),compain.gso)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        if (compain.auth().currentUser!=null){
            requireView().findNavController().navigate(R.id.action_loginFragment2_to_gameFragment2)
            this.onDestroy()
        }else{
            binding.loginButton.setOnClickListener {
                if (emptyControl()){
                    viewModel.getAuthName(view,binding.loginAuthName.text.toString(),binding.loginPassword.text.toString())
                    viewModel.progressLogin.observe(viewLifecycleOwner, Observer {
                        if (it){
                            binding.linearLayout.alpha=0.3F
                            binding.loginProgress.visibility=View.VISIBLE
                            isClicableItem(false)
                        }else{
                            binding.linearLayout.alpha=1F
                            binding.loginProgress.visibility=View.INVISIBLE
                            isClicableItem(true)
                        }
                    })
                }
            }
            binding.registerButton.setOnClickListener {
                val bundle = bundleOf("state" to "Register")
                it.findNavController().navigate(R.id.action_loginFragment2_to_registerFragment, args = bundle)
            }
            binding.signInButton.setOnClickListener {
                binding.loginProgress.visibility=View.VISIBLE
                binding.linearLayout.alpha=0.3F
                val singInIntent=mGoogleSignInClient.signInIntent
                startActivityForResult(singInIntent,1)
                binding.loginProgress.visibility=View.INVISIBLE
                binding.linearLayout.alpha=1F
            }
            binding.resetPassword.setOnClickListener {
                val  alertDialog=AlertDialog.Builder(requireContext())
                val vieww =layoutInflater.inflate(R.layout.fragment_blank,null)
                val binding =FragmentBlankBinding.bind(vieww)
                alertDialog.setView(binding.root)
                val alert =alertDialog.create()
                alert.show()
                binding.sendButton.setOnClickListener {
                    val control =emailControl(binding.eMail.text.toString())
                    if(control){
                        compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",binding.eMail.text.toString()).get().addOnSuccessListener { result->
                            if (!result.isEmpty){
                                val authNames=result.documents[0].get("Auth Name") as String
                                if (authNames.equals(binding.authName.text.toString())){
                                    compain.auth().sendPasswordResetEmail(binding.eMail.text.toString()).addOnSuccessListener {
                                        Toast.makeText(requireContext(),"Doğrulama kodu başarıyla gönderildi",Toast.LENGTH_SHORT).show()
                                        alert.cancel()
                                    }
                                }else{
                                    Toast.makeText(requireContext(),"E-Mail ile kullanıcı adı uyuşmadı",Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(requireContext(),"Böyle bir kullanıcı bulunamadı",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        Toast.makeText(requireContext(),"Lütfen e posta biçiminde giriniz",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun emailControl (email:String):Boolean{
        val patterns=Patterns.EMAIL_ADDRESS
        return patterns.matcher(email).matches()
    }

    fun isClicableItem(duraction:Boolean){
        binding.loginButton.isEnabled=duraction
        binding.linearLayout.isEnabled=duraction
        binding.loginAuthName.isEnabled=duraction
        binding.loginPassword.isEnabled=duraction
        binding.registerButton.isEnabled=duraction
    }
    fun emptyControl():Boolean{
        if (binding.loginAuthName.text.isEmpty()){
            Toast.makeText(requireContext(),"Lütfen bir kullanıcı adı giriniz",Toast.LENGTH_SHORT).show()
            return false
        }else if (binding.loginPassword.text.isEmpty()){
            Toast.makeText(requireContext(),"Lütfen bir şifrenizi giriniz",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data.let { intent->
            if (requestCode == 1) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
                if (result!!.isSuccess){
                    // Google Sign In basarili oldugunda Firebase ile yetkilendir
                    val account = result.getSignInAccount()
                    account.let {
                        compain.db().collection("Kullanıcı Bilgileri").whereEqualTo("E-Mail",account!!.email.toString()).get().addOnSuccessListener {
                            if (it.size()>0){
                                val girisYolu=it.documents[0].get("Giriş Yolu") as String
                                firebaseAuthWithGoogle(girisYolu,account)
                            }else{
                                val bundle = bundleOf("state" to "Google")
                                findNavController().navigate(R.id.action_loginFragment2_to_registerFragment, args = bundle)
                            }
                        }
                    }
                }else{
                    // Google Sign In hatası.
                    Log.e(TAG, "Google Sign In hatası.")
                }
            }
        }
    }
    private fun firebaseAuthWithGoogle(girisYolu:String,acct: GoogleSignInAccount) {
        if (girisYolu.equals("Google")){
            val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
            compain.auth().signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val intent =Intent(requireContext(),MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }else{
            mGoogleSignInClient.signOut()
            Toast.makeText(requireContext(),"Böyle bir hesap zaten bulunuyor. Kullanıcı adı ve şifre ile tekrar deneyiniz.",Toast.LENGTH_SHORT).show()
        }
        binding.linearLayout.alpha=1F
        binding.loginProgress.visibility=View.INVISIBLE
    }
    fun passwordControl(password:String):Boolean{
        val numbers ="0123456789"
        val letters ="ABCDEFGHIİJKLMNOÖPRSUÜVYZ"
        val numberControl =numbers.filter {
            password.contains(it)
        }
        val letterControl=letters.filter {
            password.contains(it)
        }
        if (letterControl.isEmpty() || numberControl.isEmpty() ){
            return true
        }else{
            return true
        }
    }
}