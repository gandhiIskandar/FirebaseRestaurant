package com.example.ayamjumpa.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ayamjumpa.util.NetworkStatusTracker
import com.example.ayamjumpa.util.NetworkStatusViewModel

//belum terpakai, mungkin nanti terpakai
class NetworkViewModelFactory(val context: Context):ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val networkStatusTracker = NetworkStatusTracker(context)
        return NetworkStatusViewModel(networkStatusTracker) as T
    }
}