package com.example.madassignment

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.TBMainAct))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.NHFMain) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        // Set up the Toolbar
        toolbar = findViewById(R.id.TBMainAct)
        setSupportActionBar(toolbar)

        // Add an OnDestinationChangedListener to handle back button visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.DestServiceList, R.id.DestAddService,
                R.id.scheduleFragment, R.id.createCommunityFragment,
                R.id.Community2, R.id.DestReportComplaint,
                R.id.addComplaintFragment, R.id.addComplaintFragment2,
                R.id.complaintFragment, R.id.complaintDetailsFragment,
                R.id.community4, R.id.Community3,
                R.id.communityChatsz-> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    toolbar.setNavigationOnClickListener {
                        navController.navigateUp()
                    }
                }
                else -> {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    toolbar.setNavigationOnClickListener(null)
                }
            }
        }
    }

    // Handle back button press in the Toolbar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
