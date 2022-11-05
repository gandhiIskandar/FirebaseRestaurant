package com.example.ayamjumpa.repository

import android.app.Application
import com.example.ayamjumpa.dataClass.User

interface AuthRepository {
 fun login(email:String, password:String):Boolean
 fun register(user: User):Boolean
}