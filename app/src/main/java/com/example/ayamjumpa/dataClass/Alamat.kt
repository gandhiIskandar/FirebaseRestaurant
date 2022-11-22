package com.example.ayamjumpa.dataClass

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Alamat (var id:String?=null,
                   var lat:Double?=null,
                   var long:Double?=null,
                   var alamat:String?=null,
                   var alamatPendek:String?=null,
                   var label:String?=null,
                   var nomorHp:String?=null,
                   var ongkir:Double?=null,
                   var checked:Boolean = false,
                   val time:Timestamp = Timestamp.now()
                   ) : Parcelable