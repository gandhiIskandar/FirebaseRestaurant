package com.example.ayamjumpa.interfaces

import com.example.ayamjumpa.dataClass.Cart

interface CartLoadListener {
    fun onLoadCartSuccess(cartModelList:List<Cart>)
    fun onLoadCartMessage(message:String?)
    fun UpdateData(cart:Cart)
    fun deleteItem(cart: Cart)
}