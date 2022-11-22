package com.example.ayamjumpa.viewModel

import android.media.Image
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ayamjumpa.dataClass.ImageData
import com.example.ayamjumpa.dataClass.Kategori
import com.example.ayamjumpa.dataClass.User
import com.example.ayamjumpa.repository.AuthRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

import kotlinx.coroutines.*

class AuthViewModel() : ViewModel() {


    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean>
        get() = _isLogin
    private val repository = AuthRepositoryImpl()

    private val _isRegistered = MutableLiveData<Boolean>()
    val isRegistered: LiveData<Boolean>
        get() = _isRegistered


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    var _user: MutableLiveData<User> = MutableLiveData<User>()
    var _total: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>()
    var _image: MutableLiveData<MutableList<ImageData>> = MutableLiveData<MutableList<ImageData>>()
    var _kateogri: MutableLiveData<MutableList<Kategori>> = MutableLiveData<MutableList<Kategori>>()

    private suspend fun _register(user: User) {
        withContext(Dispatchers.IO) {
            _isRegistered.postValue(repository.register(user))
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            _register(user)
        }
    }

    private suspend fun _login(email: String, password: String) {
        withContext(Dispatchers.Main) {
            _isLogin.value = repository.login(email, password)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _login(email, password)
        }
    }

    fun getUser(uid: String): LiveData<User> {

        val ref = Firebase.firestore.collection("users").document(uid)

        ref.addSnapshotListener { value, error ->

            if (error == null) {
                val user = value!!.toObject<User>()
                _user.value = user
            }
            else{
                Log.d("errorapasih", error.message!!)
            }


        }



        return _user

    }

    fun getImagedata(): LiveData<MutableList<ImageData>> {
        val ref = Firebase.firestore.collection("ImageData")

        val mutableData = arrayListOf<ImageData>()

        ref.addSnapshotListener { value, error ->

            if (value != null && error == null) {
                mutableData.clear()

                for (x in value) {
                    mutableData.add(x.toObject<ImageData>())
                }

                _image.value = mutableData

            }


        }

        return _image


    }

    fun getKategori(): LiveData<MutableList<Kategori>> {

        val ref = Firebase.firestore.collection("Kategori")

        val mutableData = arrayListOf<Kategori>()

        ref.addSnapshotListener { value, error ->

            if (value != null && error == null) {
                mutableData.clear()

                for (x in value) {
                    mutableData.add(x.toObject<Kategori>())
                }

                _kateogri.value = mutableData

            }


        }

        return _kateogri


    }

    fun isLoading() {
        _isLoading.value = true
    }

    fun isNotLoading() {
        _isLoading.value = false
    }

    fun setTotalItem(list: MutableList<String>) {
        _total.value = list

    }

    fun getTotal(): LiveData<MutableList<String>> {
        return _total
    }

}