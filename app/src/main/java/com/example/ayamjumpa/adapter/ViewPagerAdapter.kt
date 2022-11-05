package com.example.ayamjumpa.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ayamjumpa.fragment.DalamProsesFragment
import com.example.ayamjumpa.fragment.RiwayatFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
       var fragment = Fragment()
        when(position){
            0 -> fragment = DalamProsesFragment()
            1 -> fragment = RiwayatFragment()
        }

        return fragment
    }
}