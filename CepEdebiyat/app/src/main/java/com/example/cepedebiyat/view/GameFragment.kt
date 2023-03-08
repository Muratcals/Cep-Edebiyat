package com.example.cepedebiyat.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import com.example.cepedebiyat.GameActivity
import com.example.cepedebiyat.R
import com.example.cepedebiyat.databinding.AlertLayoutBinding
import com.example.cepedebiyat.databinding.FragmentGameBinding
import com.example.cepedebiyat.viewModel.GameViewModel

class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private lateinit var gameBinding :FragmentGameBinding
    private lateinit var shereddPreferences :SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        gameBinding=FragmentGameBinding.inflate(inflater)
        requireActivity().supportFragmentManager.beginTransaction().add(this,"Game Fragment")
        return gameBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)
        requireActivity().setTitle("Oyuna Başla")
        gameBinding.startGame.setOnClickListener {
           alertKontrol(view)
        }
        gameBinding.gameInformation.setOnClickListener {
            information()
        }
    }
    fun alertKontrol(view:View){
        shereddPreferences=requireActivity().getSharedPreferences("alert",Context.MODE_PRIVATE)
        val chechbox =shereddPreferences.getBoolean("gosterme",false)
            if (!chechbox){
                val inflater =layoutInflater.inflate(R.layout.alert_layout,null)
                val binding = AlertLayoutBinding.bind(inflater)
                val uyari =AlertDialog.Builder(view.context)
                uyari.setView(binding.root)
                val alert =uyari.show()
                binding.okey.setOnClickListener {
                    if (binding.gosterme.isChecked){
                        shereddPreferences.apply {
                            this.edit {
                                putBoolean("gosterme",true)
                            }
                        }
                    }
                    val intent = Intent(view.context,GameActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                    alert.cancel()
                }
                }else{
                    val intent = Intent(view.context,GameActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
            }
    }
    fun information(){
        val inflater =layoutInflater.inflate(R.layout.alert_layout,null)
        val binding = AlertLayoutBinding.bind(inflater)
        val uyari =AlertDialog.Builder(requireContext())
        uyari.setView(binding.root)
        val alert =uyari.show()
        binding.gostermeLayout.visibility=View.INVISIBLE
        uyari.setView(binding.root)
        binding.okey.setOnClickListener {
            alert.cancel()
        }
    }
}