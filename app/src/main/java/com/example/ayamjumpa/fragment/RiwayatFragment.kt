package com.example.ayamjumpa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ayamjumpa.adapter.PesananAdapter
import com.example.ayamjumpa.databinding.FragmentRiwayatBinding
import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RiwayatFragment : Fragment() {

    private lateinit var binding: FragmentRiwayatBinding
    private lateinit var vm:CartViewModel
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pesananAdapter: PesananAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(requireContext()),
            )[CartViewModel::class.java]
        }
        pesananAdapter = PesananAdapter()
        firebaseAuth = Firebase.auth
        vm = viewModel
        vm.reqPesanan(firebaseAuth.uid!!)
        val linearLayout = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
       binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        binding.recyclerRiwayat.layoutManager = linearLayout
        binding.recyclerRiwayat.adapter = pesananAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

     vm.getPesananx.observe(viewLifecycleOwner){
         val list = it.filter { pesanan -> pesanan.status == "selesai" }
         pesananAdapter.differ.submitList(list)

     }


    }


}