package com.example.ayamjumpa.interfaces

import com.example.ayamjumpa.dataClass.Alamat

interface Clicker {
    fun clickk(alamat: Alamat)

    fun alamatAwal(alamat:Alamat)

   fun removeAlamat(id:String)

    fun setNoHP(hp:String)

    fun clickhp(hp:String)
}