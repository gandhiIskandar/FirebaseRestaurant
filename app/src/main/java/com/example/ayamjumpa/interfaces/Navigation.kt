package com.example.ayamjumpa.interfaces

import com.example.ayamjumpa.dataClass.Menu

interface Navigation {
    fun showCart(cartCount:Int)
    fun showMenu()
    fun showRasa()
    fun showMinuman()
    fun toPesanan()
    fun toProfile()
    fun toPromo()
}