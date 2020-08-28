package com.example.android.reminder.mainActivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.reminder.R
import com.example.android.reminder.databinding.ActivityMainBinding
import com.example.android.reminder.loginActivity.LoginActivity
import com.example.android.reminder.mainActivity.mainFragment.AuthenticationState
import com.example.android.reminder.mainActivity.mainFragment.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.header.view.*
import kotlinx.android.synthetic.main.nav_header.view.*


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //========================================= Init stuff
        super.onCreate(savedInstanceState)
        val binding  = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        //========================================= setup DrawerLayout

        drawerLayout = binding.drawerLayout
        // to support navigate up
        navController = findNavController(R.id.navHostFragment)

        val navView = binding.navView


        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(navView, navController)

        // to prevent the drawer form opening in other places
        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, bundle: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        navView.setNavigationItemSelectedListener {
            if(it.itemId == R.id.log_out){
                AuthUI.getInstance().signOut(this)
                finish()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }else{
                navController.navigate(R.id.aboutFragment)
            }
            return@setNavigationItemSelectedListener true
        }

        viewModel.authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    val email = FirebaseAuth.getInstance().currentUser?.email
                    drawerLayout.navHeader.name_of_user.text = email
                }
                AuthenticationState.UNAUTHENTICATED -> {
                    Log.i(com.example.android.reminder.mainActivity.addFragment.TAG, "UNAUTHENTICATED")
                }
                else -> {
                    Log.i(com.example.android.reminder.mainActivity.addFragment.TAG, "INVALID_AUTHENTICATION")
                }
            }
        })



    }


    //========================================= setup DrawerLayout and Up button
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }



}