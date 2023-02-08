package com.example.ayamjumpa.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ayamjumpa.MainActivity
import com.example.ayamjumpa.MainMenuActivity
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.User
import com.example.ayamjumpa.databinding.FragmentMenuprofileBinding
import com.example.ayamjumpa.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MenuProfileFragment : Fragment() {

    private lateinit var binding: FragmentMenuprofileBinding
    private lateinit var viewmodel: AuthViewModel
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewmodelx: AuthViewModel by viewModels()

        auth = Firebase.auth

        viewmodel = viewmodelx
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menuprofile, container, false)

        binding.btnalamat.setOnClickListener {

            findNavController().navigate(R.id.action_menuprofile_to_blankFragment)

        }

        binding.btnpesanan.setOnClickListener {
            findNavController().navigate(R.id.action_menuprofile_to_pesanan_saya)
        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_menuprofile_to_profileFragment2)
        }

        binding.logout.setOnClickListener {
            auth.signOut()

            Intent(activity, MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewmodel.getUser(auth.uid!!)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.user.observe(viewLifecycleOwner, Observer {
            if(it!=null)
            setProfile(it)
        })
    }

   private fun setProfile(user: User) {
        Glide.with(requireContext()).load(user.foto).into(binding.fotonyaa)
        binding.usernamenyaa.text = user.username
        binding.nomornyaa.text = user.noHp
        binding.emailnyaa.text = user.email

    }


}