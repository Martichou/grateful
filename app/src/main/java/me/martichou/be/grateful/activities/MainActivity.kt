package me.martichou.be.grateful.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.ActivityMainBinding
import me.martichou.be.grateful.utilities.setupPermissions

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    /**
     * Use navigation framework to manage our fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.main_notes_nav_fragment)

        setupPermissions(this, this)
    }
}