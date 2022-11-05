package com.example.ayamjumpa.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart( var key:String?=null,
                 var name:String?=null,
                 var foto:String?=null,
                 var harga:String?=null,
                 var qty :Int=0,
                 var totalharga: Long =0L):Parcelable

