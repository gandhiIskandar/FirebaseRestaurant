package com.example.ayamjumpa.viewModel

import android.content.Context
import androidx.lifecycle.*
import com.example.ayamjumpa.dataClass.Alamat
import com.example.ayamjumpa.dataClass.Cart
import com.example.ayamjumpa.dataClass.Menu
import com.example.ayamjumpa.dataClass.Pesanan
import com.example.ayamjumpa.repository.DataStoreRepository
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class CartViewModel(context: Context) : ViewModel() {

    private val documentId="B5zlAROLSZFjfiU8hVPD"

    private val dataStoreRepository: DataStoreRepository = DataStoreRepository(context)
    private var alamatku = MutableLiveData<Alamat>()

    private var getPesanan = MutableLiveData<MutableList<Pesanan>>()
    val getPesananx: LiveData<MutableList<Pesanan>>
        get() = getPesanan

   private var _adminToken = MutableLiveData<String?>()
    val adminToken:LiveData<String?>
    get() = _adminToken

    val firebaseFirestore = Firebase.firestore
    val alamatkuX: LiveData<Alamat>
        get() = alamatku

    var nomorkuX: LiveData<String>? = null

    var koneksi: LiveData<Boolean>? = null

    var menuStok: Int = 0


    fun setAlamat(alamat: Alamat) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveAlamat(
                Alamat(
                    alamat = alamat.alamat,
                    ongkir = alamat.ongkir,
                    lat = alamat.lat,
                    long = alamat.long,
                    id = alamat.id,
                    nomorHp = alamat.nomorHp,
                    label = alamat.label
                )
            )
        }
    }

    fun setNomor(nomor: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveNomor(nomor)
        }

    }

    fun setKoneksi(data: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.setKoneksi(data)

        }
    }

    fun getKoneksi() {
        viewModelScope.launch(Dispatchers.IO) {
            koneksi = dataStoreRepository.getKoneksi().asLiveData()
        }

    }


    fun getNomor() {
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

    fun clearAlamat() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.removeAlamat()

        }
    }

    fun reqPesanan(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val temp = mutableListOf<Pesanan>()
            firebaseFirestore.collection("Pesanan").orderBy("tanggal", Query.Direction.DESCENDING)
                .get()

                .addOnCompleteListener {
                    if (it.isSuccessful && it.result.size() > 0) {

                        val res = it.result

                        res.forEach { pesanan ->
                            if (pesanan.toObject<Pesanan>().idPelanggan == uid) {
                                temp.add(pesanan.toObject<Pesanan>())
                            }

                        }

                    }
                    getPesanan.postValue(temp)
                }
        }
    }


    fun getToken(){
        viewModelScope.launch(Dispatchers.IO){
            firebaseFirestore.collection("AdminToken").document(documentId).addSnapshotListener { value, error ->

                if(error==null && value!=null){
                   val token = value.get("token").toString()

                    _adminToken.postValue(token)
                }else {
                    _adminToken.postValue(null)
                }

            }
        }
    }

    sealed class StockChecker {
        object Proses : StockChecker()
        object Habis : StockChecker()
        object Kurang : StockChecker()
        object Initial : StockChecker()

    }


}