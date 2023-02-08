package com.example.ayamjumpa.dataClass

import com.google.firebase.Timestamp

data class ImageData(
    var id:String="",
    var imageUrl: String = "",
    var time: Timestamp? = null
)