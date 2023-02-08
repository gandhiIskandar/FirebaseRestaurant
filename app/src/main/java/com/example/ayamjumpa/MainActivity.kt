package com.example.ayamjumpa

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navController = navHostFragment.navController

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        setupActionBarWithNavController(navController)

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }

        initLabelFragment()


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun initLabelFragment() {
        navController.addOnDestinationChangedListener { _, destination, _ ->

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

                Log.d("kontol", destination.label.toString())
                supportActionBar?.setTitle(Html.fromHtml("<font color=\"#332C2B\" ><b>" + destination.label.toString() + "</b></font>"))
            }


        }
    }


}