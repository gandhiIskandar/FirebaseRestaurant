package com.example.ayamjumpa.viewModel

import android.app.PendingIntent
import android.content.Context
import androidx.lifecycle.*
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.repository.DataStoreRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch



class CartViewModel(context: Context) : ViewModel() {

    private val dataStoreRepository: DataStoreRepository = DataStoreRepository(context)
    private var alamatku = MutableLiveData<Alamat>()

    private var getPesanan=MutableLiveData<MutableList<Pesanan>>()
    val getPesananx: LiveData<MutableList<Pesanan>>
        get() = getPesanan


    val firebaseFirestore = Firebase.firestore
    val alamatkuX: LiveData<Alamat>
        get() = alamatku

    var nomorkuX: LiveData<String>?=null



    fun setAlamat(alamat: Alamat) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveAlamat(
                Alamat(
                    alamat = alamat.alamat,
                    ongkir = alamat.ongkir,
                    lat = alamat.lat,
                    long = alamat.long,
                    id = alamat.id,
                    keterangan = alamat.keterangan,
                    label = alamat.label
                )
            )
        }
    }

    fun setNomor(nomor:String){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveNomor(nomor)
        }

    }

    fun getNomor(){
        viewModelScope.launch(Dispatchers.IO) {
           nomorkuX = dataStoreRepository.getNomor().asLiveData()
        }
    }

    fun getAlamat() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.getAlamat().collect {
                alamatku.postValue(it)
            }
        }
    }

    fun clearAlamat(){
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.removeAlamat()

        }
    }

    fun reqPesanan(uid:String){
        viewModelScope.launch(Dispatchers.IO) {
            val temp = mutableListOf<Pesanan>()
            firebaseFirestore.collection("Pesanan").orderBy("tanggal",Query.Direction.DESCENDING).get()

                .addOnCompleteListener {
                    if(it.isSuccessful && it.result.size()>0){

                       val res = it.result

                        res.forEach { pesanan ->
                            if(pesanan.toObject<Pesanan>().idPelanggan == uid){
                               temp.add(pesanan.toObject<Pesanan>())
                            }

                        }

                    }
                    getPesanan.postValue(temp)
                }
        }
    }


}