package com.example.ayamjumpa.dataClass

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Alamat (var id:String?=null,
                   var lat:Double?=null,
                   var long:Double?=null,
                   var alamat:String?=null,
                   var alamatPendek:String?=null,
                   var label:String?=null,
                   var keterangan:String?=null,
                   var ongkir:Double?=null,
                   var expandview:Boolean = false) : Parcelable