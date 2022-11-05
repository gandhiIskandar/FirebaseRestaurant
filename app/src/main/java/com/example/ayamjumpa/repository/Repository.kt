package com.example.ayamjumpa.repository


import androidx.lifecycle.MutableLiveData
import com.example.ayamjumpa.dataClass.Cart
import com.example.ayamjumpa.dataClass.Menu


interface Repository {



    fun addToCart(menu: Menu, uid:String) :String
    fun loadCart(uid:String)
    fun loadTerlaris():MutableLiveData<MutableList<Menu>>
    fun updateCart(cart: Cart,uid: String)




}