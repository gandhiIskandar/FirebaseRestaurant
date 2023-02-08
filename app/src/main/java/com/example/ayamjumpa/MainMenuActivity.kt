package com.example.ayamjumpa

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import com.example.ayamjumpa.util.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ayamjumpa.viewModel.CartViewModel
import com.example.ayamjumpa.viewModel.CartViewModelFactory
import com.example.ayamjumpa.viewModel.NetworkViewModelFactory
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase



class MainMenuActivity : AppCompatActivity() {


    private lateinit var vm: CartViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private lateinit var networkStatusViewModel: NetworkStatusViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_main_menu)

        val builder = AlertDialog.Builder(this)

        builder.setView(R.layout.offline_dialog)

        val networkStatus: NetworkStatusViewModel by lazy {
            ViewModelProvider(
                this,
                NetworkViewModelFactory(this),
            )[NetworkStatusViewModel::class.java]
        }

        networkStatusViewModel = networkStatus


        val viewModel: CartViewModel by lazy {
            ViewModelProvider(
                this,
                CartViewModelFactory(this),
            )[CartViewModel::class.java]
        }

        vm = viewModel

        vm.setKoneksi(false)


        dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        //    dialog.show()

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView2) as NavHostFragment

        navController = navHostFragment.navController






            getLocation()




        setupActionBarWithNavController(navController)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        val defaultAppBar = supportActionBar?.customView
        val defaultAppBardisplay = supportActionBar?.displayOptions

        navController.addOnDestinationChangedListener {_, destination, _ ->


            if (destination.label == "home") {

                supportActionBar?.displayOptions =
                    androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
                supportActionBar?.setCustomView(R.layout.supportactionbar)


            } else {

                supportActionBar?.displayOptions = defaultAppBardisplay!!
                supportActionBar?.customView = defaultAppBar

                supportActionBar?.setDisplayHomeAsUpEnabled(true)


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back1)
                    supportActionBar?.setTitle(
                        Html.fromHtml(
                            "<font color=\"#332C2B\" ><b>" + destination.label.toString() + "</b></font>",
                            Html.FROM_HTML_MODE_LEGACY
                        )
                    )
                } else {
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back1)
                    supportActionBar?.setTitle(Html.fromHtml("<font color=\"#332C2B\" ><b>" + destination.label.toString() + "</b></font>"))
                }
            }
        }



        networkStatus.state.observe(this) {


            if (it != MyState.Fetched) {
                vm.setKoneksi(false)
                dialog.show()
            } else {
                vm.setKoneksi(true)
                dialog.dismiss()
            }


        }


    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    //previous fragment ketika klik backbutton
    override fun onBackPressed() {
        if (navHostFragment.childFragmentManager.backStackEntryCount > 0) {
            navController.popBackStack()

        } else {
            super.onBackPressed()
        }


    }

    //func sudah dipanggil di onCreate

    @SuppressLint("MissingPermission")
    private fun setupListener() {
        val fusedProvider = LocationServices.getFusedLocationProviderClient(this)
        val mLocationRequest = LocationRequest.Builder(500)
        mLocationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        val locationListener = mLocationRequest.build()

        val callback = object: LocationCallback(){
            override fun onLocationAvailability(p0: LocationAvailability) {

            }

            override fun onLocationResult(mlocation: LocationResult) {
             for(location in mlocation.locations){
                 if(location != null){
                     fusedProvider.removeLocationUpdates(this)
                     networkStatusViewModel.setCurrentLocation(location)
                 }
             }
            }
        }

        fusedProvider.requestLocationUpdates(locationListener ,callback, null)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            1000 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocation()

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }




    private fun getLocation() {

            if (ActivityCompat.checkSelfPermission(
                    this@MainMenuActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this@MainMenuActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this@MainMenuActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    1000
                )

                return

            } else {

                LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener {
                    if(it ==null){
                        setupListener()
                    }else{
                        Log.d("locationb", it.longitude.toString())
                        networkStatusViewModel.setCurrentLocation(it)
                    }
                }


            }
        }






}