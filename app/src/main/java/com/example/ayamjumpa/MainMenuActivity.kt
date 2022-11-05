package com.example.ayamjumpa

import com.example.ayamjumpa.util.*

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ayamjumpa.interfaces.MenuListener
import com.example.ayamjumpa.viewModel.NetworkViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint


class MainMenuActivity : AppCompatActivity() {

    private lateinit var menuListener: MenuListener

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private val viewModel: NetworkStatusViewModel by lazy {
        ViewModelProvider(
            this,
            NetworkViewModelFactory(this),
        )[NetworkStatusViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_main_menu)




      navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView2) as NavHostFragment

        navController = navHostFragment.navController

//        val bottomNavigation = findViewById<BottomNavigationView>(R.id.botnav)
//
//        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.menuprofile, R.id.menuFragment, R.id.cartFragment2))

        setupActionBarWithNavController(navController)

        // bottomNavigation.setupWithNavController(navController)





        viewModel.state.observe(this) {
            Snackbar.make(
                findViewById(R.id.fragmentContainerView2), when (it) {
                    MyState.Fetched -> "Terhubung"
                    MyState.Error -> "Tidak Terhubung"

                }, Snackbar.LENGTH_LONG
            ).show()
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if(navHostFragment.childFragmentManager.backStackEntryCount>0){
            navController.popBackStack()

        }else{
            super.onBackPressed()
        }







    }
}