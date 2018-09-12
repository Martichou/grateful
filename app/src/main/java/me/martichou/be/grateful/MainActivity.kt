package me.martichou.be.grateful

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import androidx.navigation.Navigation
import me.martichou.be.grateful.databinding.MainActivityBinding

class MainActivity : FragmentActivity() {

    /**
     * Use navigation framework to manage our fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding = DataBindingUtil.setContentView(
            this,
            R.layout.main_activity
        )

        Navigation.findNavController(this, R.id.notes_nav_fragment)
    }
}
