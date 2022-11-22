package com.example.ayamjumpa

import android.app.ActionBar
import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import com.example.ayamjumpa.util.*

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentOnAttachListener
import androidx.lifecycle.ViewModelProvider

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ayamjumpa.interfaces.MenuListener
import com.example.ayamjumpa.viewModel.NetworkViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


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

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        val defaultAppBar = supportActionBar?.customView
        val defaultAppBardisplay = supportActionBar?.displayOptions

        navController.addOnDestinationChangedListener { controller, destination, arguments ->


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
                    supportActionBar?.setTitle(Html.fromHtml("<font color=\"#332C2B\" align=\"center\" ><b>" + destination.label.toString() + "</b></font>"))
                }
            }
        }






        viewModel.state.observe(this) {
//            Snackbar.make(
//                findViewById(R.id.fragmentContainerView2), when (it) {
//                    MyState.Fetched -> "Terhubung"
//                    MyState.Error -> "Tidak Terhubung"
//
//                }, Snackbar.LENGTH_LONG
//            ).show()
        }


    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {


        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
    }

    override fun getSupportActionBar(): androidx.appcompat.app.ActionBar? {
        return super.getSupportActionBar()
    }

    //previous fragment ketika klik backbutton
    override fun onBackPressed() {
        if (navHostFragment.childFragmentManager.backStackEntryCount > 0) {
            navController.popBackStack()

        } else {
            super.onBackPressed()
        }


    }
}