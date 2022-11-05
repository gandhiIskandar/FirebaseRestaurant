package com.example.ayamjumpa.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.ayamjumpa.R
import com.example.ayamjumpa.adapter.ViewPagerAdapter
import com.example.ayamjumpa.databinding.FragmentPesananSayaBinding
import com.example.ayamjumpa.eventBus.StatusMessage
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class PesananSayaFragment : Fragment() {


    private lateinit var binding:FragmentPesananSayaBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pesanan_saya, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerAdapter = ViewPagerAdapter(this)


        with(binding){
            viewPager.adapter = viewPagerAdapter

            TabLayoutMediator(tabLayout, viewPager){ tab, postion ->

                when(postion){
                    0-> tab.text ="Sedang dalam proses"
                    1-> tab.text ="Riwayat"
                }

            }.attach()



        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun goToDetail(statusMessage: StatusMessage) {
        //ke halaman detail argumen pesanan
        statusMessage.getPesanan()

    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


}