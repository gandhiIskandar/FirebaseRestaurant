package com.example.ayamjumpa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ayamjumpa.R
import com.example.ayamjumpa.adapter.PesananAdapter
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.databinding.FragmentDalamProsesBinding
import com.example.ayamjumpa.eventBus.StatusMessage
import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus


class DalamProsesFragment : Fragment() {


    private lateinit var binding:FragmentDalamProsesBinding
    private lateinit var vm: CartViewModel
    private lateinit var auth:FirebaseAuth
    private lateinit var pesananAdapter:PesananAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = Firebase.auth
        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(requireContext()),
            )[CartViewModel::class.java]
        }
        binding = FragmentDalamProsesBinding.inflate(inflater,container,false)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.pesananRecycler.layoutManager = linearLayoutManager
       pesananAdapter = PesananAdapter(
           onClick = {
               onClickDetail(it)
           }
       )
        binding.pesananRecycler.adapter = pesananAdapter

        vm = viewModel

        vm.reqPesanan(auth.uid!!)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        vm.getPesananx.observe(viewLifecycleOwner){pesanan->
            val list = pesanan.filter { pesanan-> pesanan.status == "proses" }
            pesananAdapter.differ.submitList(list)

        }
    }

    private fun onClickDetail(pesanan: Pesanan){
        val statusMessage = StatusMessage("pindah")
        statusMessage.setPesanan(pesanan)
        EventBus.getDefault().post(statusMessage)
    }
}