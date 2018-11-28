package me.martichou.be.grateful

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.AppBarLayout
import me.martichou.be.grateful.databinding.MainActivityBinding
import kotlin.math.absoluteValue

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    /**
     * Use navigation framework to manage our fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: MainActivityBinding = DataBindingUtil.setContentView(this,
                R.layout.main_activity)

        navController = Navigation.findNavController(this, R.id.main_notes_nav_fragment)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)

        /*binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
            val appBarOffset = offset.absoluteValue
            val currentElevation = if (appBarOffset >= resources.getDimensionPixelSize(R.dimen.toolbar_height)) resources.getDimensionPixelSize(R.dimen.app_bar_elevation).toFloat() else 0f
            if (ViewCompat.getElevation(appBarLayout) != currentElevation) ViewCompat.setElevation(appBarLayout, currentElevation)
        })*/
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}