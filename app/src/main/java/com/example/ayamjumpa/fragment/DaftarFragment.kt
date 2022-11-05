package com.example.ayamjumpa.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ayamjumpa.MainMenuActivity
import com.example.ayamjumpa.R
import com.example.ayamjumpa.dataClass.User
import com.example.ayamjumpa.databinding.FragmentDaftarBinding
import com.example.ayamjumpa.eventBus.StatusMessage
import com.example.ayamjumpa.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@Suppress("RedundantIf")
class DaftarFragment : Fragment() {

    private lateinit var binding: FragmentDaftarBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseAuth = Firebase.auth
        fireStore = Firebase.firestore

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daftar, container, false)

        binding.daftar.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val password1 = binding.password1.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val nohp = binding.nomorHp.text.toString().trim()

            if (password == password1) {
                authViewModel.register(User(email, password, username, noHp=nohp, isAdmin = false))
                keHalamanLogin()
            } else {
                Toast.makeText(
                    activity,
                    "Password tidak sama",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        val watcher: TextWatcher = object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.email.text.isNotEmpty() && binding.password.text.isNotEmpty() && binding.password1.text.isNotEmpty() &&
                    binding.username.text.isNotEmpty() && binding.nomorHp.text.isNotEmpty()
                ) {
                    binding.daftar.isEnabled = true



                } else {
                    binding.daftar.isEnabled = false


                }
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                //YOUR CODE
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        binding.email.addTextChangedListener(watcher)
        binding.username.addTextChangedListener(watcher)
        binding.nomorHp.addTextChangedListener(watcher)
        binding.password.addTextChangedListener(watcher)
        binding.password1.addTextChangedListener(watcher)


        return binding.root

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gagalDaftar(statusMessage: StatusMessage) {
        Snackbar.make(
            requireView(),
            "Gagal Daftar: " + statusMessage.getMessage(),
            Snackbar.LENGTH_SHORT
        ).show()

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun keHalamanLogin() {
        val bundle  = bundleOf("berhasil" to "Berhasil Daftar, Sialhkan Login")
        findNavController().navigate(R.id.action_daftarFragment_to_loginFragment, bundle)
    }


}