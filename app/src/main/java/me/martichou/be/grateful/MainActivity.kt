package me.martichou.be.grateful

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupActionBarWithNavController
import me.martichou.be.grateful.databinding.MainActivityBinding
import me.martichou.be.grateful.fragments.dialogFragment.BottomsheetFragment
import me.martichou.be.grateful.utilities.setupPermissions

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    /**
     * Use navigation framework to manage our fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<MainActivityBinding>(this, R.layout.main_activity).apply {
            this.hdl = this@MainActivity
        }

        navController = Navigation.findNavController(this, R.id.main_notes_nav_fragment)

        setupPermissions(this, this)
    }

    /**
     * Open the bottomsheet
     */
    fun btnNewAction(view: View) {
        val bottomsheetFragment = BottomsheetFragment()
        bottomsheetFragment.show(supportFragmentManager, bottomsheetFragment.tag)
    }

}