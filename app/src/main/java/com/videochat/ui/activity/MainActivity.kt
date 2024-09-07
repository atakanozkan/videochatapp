package com.videochat.ui.activity

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.videochat.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.main_activity)
        }catch (e : Exception) {
            Log.e(TAG, "onCreateView", e);
            throw e;
        }
        //val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_graph) as NavHostFragment
        //navController = navHostFragment.navController
    }
}
