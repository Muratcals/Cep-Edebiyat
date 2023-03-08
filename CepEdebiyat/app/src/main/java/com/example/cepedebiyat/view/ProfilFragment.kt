package com.example.cepedebiyat.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cepedebiyat.MainActivity
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.FragmentProfilBinding
import com.example.cepedebiyat.unit.compain
import com.example.cepedebiyat.viewModel.ProfilViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID

class ProfilFragment : Fragment() {

    private lateinit var viewModel: ProfilViewModel
    private lateinit var binding:FragmentProfilBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentProfilBinding.inflate(inflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Profil Fragment")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setTitle("Profil")
        mGoogleSignInClient=GoogleSignIn.getClient(requireActivity(),compain.gso)
        viewModel = ViewModelProvider(this).get(ProfilViewModel::class.java)
        viewModel.getAuthInformation()
        observerItem()
        binding.refreshProfil.setOnRefreshListener {
            viewModel.getAuthInformation()
            viewModel.updateProfilImage(binding)
            binding.refreshProfil.isRefreshing=false
        }
        viewModel.updateProfilImage(binding)
        viewModel.information.observe(viewLifecycleOwner, Observer {
            if (!it.isEmpty()){
                binding.errorLinear.visibility=View.INVISIBLE
                binding.profilLinear.visibility=View.VISIBLE
                val oran =(it[0].dogruSayisi.toDouble()/it[0].toplamSoru.toDouble())*100
                binding.profilName.text=it[0].name
                binding.profilSoru.text=it[0].toplamSoru.toString()
                binding.profilScore.text=it[0].puan.toString()
                if (it[0].toplamSoru.toInt()!=0){
                    binding.profilOran.text="${Math.floor((oran))}%"
                }else{
                    binding.profilOran.text="0%"
                }
            }else{
                binding.errorLinear.visibility=View.VISIBLE
                binding.profilLinear.visibility=View.INVISIBLE
            }
        })
        binding.refreshButton.setOnClickListener {
            viewModel.getAuthInformation()
            viewModel.updateProfilImage(binding)
        }
        viewModel.profilProgress.observe(viewLifecycleOwner, Observer {
            if(it){
                binding.profilLinear.visibility=View.INVISIBLE
                binding.profilProgress.visibility=View.VISIBLE
            }else{
                binding.profilLinear.visibility=View.VISIBLE
                binding.profilProgress.visibility=View.INVISIBLE
            }
        })
        binding.profilLogout.setOnClickListener {
            profilLogoutAlert()
        }
        binding.profilDelete.setOnClickListener {
            viewModel.deleteAccount(view)
        }
        binding.profilDuzenle.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment2_to_profilDuzenleFragment)
        }
        binding.share.setOnClickListener {
            viewModel.shareApp(view)
        }
        binding.profilImage.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment2_to_avatarSelectedFragment)
            /*
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(),"İzin verilmedi",Toast.LENGTH_SHORT).show()
            }else{
                val intent =Intent(Intent.ACTION_GET_CONTENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }

             */
        }
        binding.profilSave.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment2_to_saveFragment)
        }
    }
    fun observerItem(){
        viewModel.deleteAccountError.observe(viewLifecycleOwner, Observer {
            if (!it){
                val action=Intent(requireContext(),MainActivity::class.java)
                startActivity(action)
                requireActivity().finish()
            }
        })
        viewModel.profilProgress.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.profilProgress.visibility=View.VISIBLE
            }else{
                binding.profilProgress.visibility=View.INVISIBLE
            }
        })
    }
    fun profilLogoutAlert(){
        val alert= AlertDialog.Builder(requireContext())
        alert.setMessage("Oturumu kapatmak istediğinize emin misiniz?")
        alert.setTitle("Oturumu Kapat")
        alert.setPositiveButton("Evet", DialogInterface.OnClickListener { dialog, which ->
            val cikis =viewModel.authLogout()
            if (cikis){
                compain.auth().signOut()
                if (mGoogleSignInClient!=null){
                    mGoogleSignInClient.signOut()
                }
                val action = Intent(requireContext(), MainActivity::class.java)
                startActivity(action)
                activity?.finish()
            }else{
                Toast.makeText(requireContext(),"Bir hata oluştu",Toast.LENGTH_SHORT).show()
            }
        })
        alert.setNegativeButton("Hayır",DialogInterface.OnClickListener { dialog, which ->

        })
        alert.show()
    }

}