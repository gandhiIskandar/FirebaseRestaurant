package com.example.ayamjumpa.util
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide.init
import kotlinx.coroutines.*
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

class CheckNetworkConnection (private val connectivityManager: ConnectivityManager) : LiveData<Boolean>() {

    constructor(application: Application) : this(application.getSystemService(Context.CONNECTIVITY_SERVICE) as  ConnectivityManager)
    private val networkCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)

    object : ConnectivityManager.NetworkCallback(){



        override fun onAvailable(network: Network) {


            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)


            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                      postValue(true)
                    }
                }
            }
        }


        override fun onLost(network: Network) {
        super.onLost(network)
            postValue(false)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(builder.build(),networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }


    object DoesNetworkHaveInternet {

        fun execute(socketFactory: SocketFactory): Boolean {
            // Make sure to execute this on a background thread.
            return try {

                val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()

                true
            } catch (e: IOException) {

                false
            }
        }
    }
}

