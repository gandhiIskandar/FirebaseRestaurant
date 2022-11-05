package com.example.ayamjumpa.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ayamjumpa.util.NetworkStatusTracker
import com.example.ayamjumpa.util.NetworkStatusViewModel

class CartViewModelFactory(val context: Context): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartViewModel(context) as T
    }
}