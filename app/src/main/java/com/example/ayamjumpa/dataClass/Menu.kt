package com.example.ayamjumpa.dataClass

 data class Menu(
     var key:String? = null,
     var nama:String? = null,
     var harga:Long= 0L,
     var tipe:String?=null,
     var foto:String? =null,
     var deskripsi:String? =null,
     var menuKey:String? =null,
     var rasaKey:String? =null,
     var terjual:Long?=null,
     var deskripsi1:String?=null,
     var promo:Boolean=false,
     var rekom:Boolean=false,
     var potongan:Long=0L,
     var tipeMinuman:String?=null,
     var stok:Long=0L
 )