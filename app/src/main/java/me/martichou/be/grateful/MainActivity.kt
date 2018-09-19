package me.martichou.be.grateful

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import me.martichou.be.grateful.databinding.MainActivityBinding
import me.martichou.be.grateful.utilities.setupPermissions

class MainActivity : FragmentActivity() {

    /**
     * Use navigation framework to manage our fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupPermissions(this, this)

        val binding: MainActivityBinding = DataBindingUtil.setContentView(
            this,
            R.layout.main_activity
        )

        Navigation.findNavController(this, R.id.notes_nav_fragment)
    }
}