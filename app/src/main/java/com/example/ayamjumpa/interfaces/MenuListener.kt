package com.example.ayamjumpa.interfaces

import com.example.ayamjumpa.dataClass.Menu

interface MenuListener {
    fun onMenuLoadSucces(menuList: MutableList<Menu>)
    fun onMenuLoadFailed(message:String?)

}