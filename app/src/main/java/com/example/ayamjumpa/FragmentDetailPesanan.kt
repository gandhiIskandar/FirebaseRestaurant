package com.example.ayamjumpa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ayamjumpa.databinding.FragmentDetailPesananBinding


class FragmentDetailPesanan : Fragment() {
   private lateinit var binding:FragmentDetailPesananBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        binding = FragmentDetailPesananBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }


}