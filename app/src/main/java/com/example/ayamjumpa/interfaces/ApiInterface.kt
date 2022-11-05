package com.example.ayamjumpa.interfaces

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("maps/api/distancematrix/json")
    fun getDistance(@Query("key") key:String,
                    @Query("origins")origin:String,
                    @Query("destinations") destination:String
                    )



}