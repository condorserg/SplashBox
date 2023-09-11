package com.condor.splashbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.condor.splashbox.data.collection.CollectionsFragment
import com.condor.splashbox.data.user.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Получаем менеджер фрагментов
        supportFragmentManager

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragment
        ) as NavHostFragment
        navController = navHostFragment.navController

        // Setup the bottom navigation view with navController
        val navView = findViewById<BottomNavigationView>(R.id.navigation)
        navView.setupWithNavController(navController)

        //show BottomNavBar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_photos) {
                navView.isVisible = true
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_photos, R.id.collectionsFragment, R.id.navigation_user
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.isVisible = false
    }
}