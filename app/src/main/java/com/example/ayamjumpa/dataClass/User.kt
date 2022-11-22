package com.example.ayamjumpa.dataClass

data class User (var email: String="",
                 var password: String = "",
                 var username: String = "",
                 var noHPlain: List<String>? = null,
                 var noHp: String? = null,
                 var isAdmin: Boolean = false,
                 var foto: String? = null
) {




}