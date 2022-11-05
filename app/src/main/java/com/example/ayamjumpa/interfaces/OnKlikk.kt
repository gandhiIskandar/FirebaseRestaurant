package com.example.ayamjumpa.interfaces

interface OnKlikk<T> {

    fun onClickEdit(data:T){
        println("duarrr")
    }

    fun onClickHapus(data: T)
}