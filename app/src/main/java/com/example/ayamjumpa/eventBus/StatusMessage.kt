package com.example.ayamjumpa.eventBus

import com.example.ayamjumpa.dataClass.Pesanan

data class StatusMessage(val msg:String){

    private var _msg : String = msg
    private var pesanan:Pesanan?=null


    fun getMessage():String{
        return _msg
    }

    fun setPesanan(pesanan: Pesanan){
        this.pesanan = pesanan
    }

    fun getPesanan():Pesanan = pesanan!!



}


