package com.example.ayamjumpa.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.ayamjumpa.dataClass.Alamat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val PREFERENCE_NAME = "my_preference"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCE_NAME)

class DataStoreRepository(private val context: Context) {

    companion object {

        val ALAMAT = stringPreferencesKey("alamat")
        val ID = stringPreferencesKey("id")
        val ONGKIR = doublePreferencesKey("ongkir")
        val LAT = doublePreferencesKey("lat")
        val LONG = doublePreferencesKey("long")
        val NOHP = stringPreferencesKey("keterangan")
        val LABEL = stringPreferencesKey("label")

        val KONEKSI = booleanPreferencesKey("koneksi")

        //terpisah
        val NOMORHP = stringPreferencesKey("noHp")



    }

    suspend fun saveAlamat(alamatt: Alamat) {
        context.dataStore.edit { alamat ->

            alamat[ALAMAT] = alamatt.alamat!!
            alamat[ONGKIR] = alamatt.ongkir!!
            alamat[LAT] = alamatt.lat!!
            alamat[LONG] = alamatt.long!!
            alamat[ID] = alamatt.id!!
            alamat[NOHP] = alamatt.nomorHp!!
            alamat[LABEL] = alamatt.label!!


        }


    }

    suspend fun saveNomor(nomorr:String){
        context.dataStore.edit { nomor->

            nomor[NOMORHP] = nomorr

        }

    }

    suspend fun setKoneksi(boolean:Boolean){
        context.dataStore.edit { koneksi->
            koneksi[KONEKSI] = boolean

        }
    }

    fun getKoneksi():Flow<Boolean>{
      return context.dataStore.data.map { dataaing ->
          dataaing[KONEKSI]?:false
      }
    }

    suspend fun getNomor():Flow<String>{
        return context.dataStore.data.map { dataaing ->
            dataaing[NOMORHP]?: String()
        }
    }

    suspend fun removeNomor(){
        context.dataStore.edit {
            it.remove(NOMORHP)
        }
    }
suspend fun removeAlamat(){
    context.dataStore.edit {
        it.remove(ALAMAT)
        it.remove(ONGKIR)
        it.remove(LAT)
        it.remove(LONG)
        it.remove(ID)
    }
}

    suspend fun getAlamat(): Flow<Alamat> {


        return context.dataStore.data.map { phonebook ->
            Alamat(
                alamat = phonebook[ALAMAT],
                ongkir = phonebook[ONGKIR],
                lat = phonebook[LAT],
                long = phonebook[LONG],
               id = phonebook[ID],
                nomorHp = phonebook[NOHP],
                label = phonebook[LABEL]
            )

        }

    }


}