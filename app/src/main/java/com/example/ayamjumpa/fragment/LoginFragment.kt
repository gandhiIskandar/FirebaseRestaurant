package com.example.ayamjumpa.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.ayamjumpa.MainMenuActivity
import com.example.ayamjumpa.R
import com.example.ayamjumpa.databinding.FragmentLoginBinding
import com.example.ayamjumpa.eventBus.StatusMessage
import com.example.ayamjumpa.util.AlertDialogBuilder
import com.example.ayamjumpa.viewModel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val authh=FirebaseAuth.getInstance()
    private val scope = CoroutineScope(Job() + Dispatchers.IO)


    private val handler : Handler by lazy{
        Handler(Looper.getMainLooper())
    }

    private lateinit var mContext : Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        firebaseAuth = Firebase.auth

        val loading = AlertDialogBuilder(requireActivity())


        if (firebaseAuth.currentUser != null) {
            masukMenuUtama()
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.signup.setOnClickListener {

            findNavController().navigate(R.id.action_loginFragment_to_daftarFragment)
        }
        binding.login.setOnClickListener {

            val email = binding.emailLogin.text.toString().trim()
            val password = binding.passwordLogin.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                val runnable = timeOut(loading)

                loading.startAlertDialog("Loading..")

                handler.postDelayed(runnable, 10000)

                scope.launch {
                  authh.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                       if(it.isSuccessful){
                           handler.removeCallbacks(runnable)
                           masukMenuUtama()
                       }else{

                           handler.removeCallbacks(runnable)
                           loading.dismiss()

                           Snackbar.make(
                               requireView(),
                               "Gagal Login",
                               Snackbar.LENGTH_SHORT
                           ).show()

                       }
                   }



                }


            }


        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments?.getString("berhasil") != null) {
            Snackbar.make(requireView(), arguments?.getString("berhasil")!!, Snackbar.LENGTH_SHORT)
                .show()
        }
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gagalLogin(statusMessage: StatusMessage) {
        Snackbar.make(
            requireView(),
            "Gagal Login: " + statusMessage.getMessage(),
            Snackbar.LENGTH_SHORT
        ).show()

    }

    private fun timeOut(loading:AlertDialogBuilder) = Runnable{
        Toast.makeText(mContext, "Gagal login, mohon periksa koneksi internet anda", Toast.LENGTH_SHORT).show()
        loading.dismiss()

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

    private fun masukMenuUtama() {
        Intent(activity, MainMenuActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
}