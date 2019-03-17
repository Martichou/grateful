package me.martichou.be.grateful.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AestheticActivity
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.ActivityMainBinding
import me.martichou.be.grateful.utilities.setupPermissions

class MainActivity : AestheticActivity() {

    private lateinit var navController: NavController

    /**
     * Use navigation framework to manage our fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.main_notes_nav_fragment)

        setupPermissions(this, this)

        if (Aesthetic.isFirstTime) {
            Aesthetic.config {
                isDark(false)
                textColorPrimary(res = R.color.black)
                textColorSecondary(res = R.color.grey_light)
                textColorSecondaryInverse(res = R.color.grey_lightlight)
                colorPrimary(res = R.color.white)
                colorAccent(res = R.color.red)
                colorWindowBackground(res = R.color.white)
                colorStatusBarAuto()
                colorNavigationBarAuto()
                textColorPrimary(Color.BLACK)
                bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
                bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
                swipeRefreshLayoutColorsRes(R.color.red)

                snackbarBackgroundColorDefault()
                snackbarTextColorDefault()
            }
        }
    }
}