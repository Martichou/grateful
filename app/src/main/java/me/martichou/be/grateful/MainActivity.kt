package me.martichou.be.grateful

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AestheticActivity
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import me.martichou.be.grateful.databinding.ActivityMainBinding
import me.martichou.be.grateful.util.notifications.NotificationHelper
import javax.inject.Inject

class MainActivity : AestheticActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private lateinit var navController: NavController

    /**
     * Use navigation framework to manage our fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.main_notes_nav_fragment)

        if (Aesthetic.isFirstTime) {
            Aesthetic.config {
                isDark(false)
                textColorPrimary(res = R.color.almostblack)
                textColorSecondary(res = R.color.grey_light)
                textColorSecondaryInverse(res = R.color.grey_lightlight)
                colorPrimary(res = R.color.white)
                colorAccent(res = R.color.red)
                colorWindowBackground(res = R.color.white)
                colorStatusBar(res = R.color.white)
                colorNavigationBarAuto()
                colorCardViewBackground(res = R.color.fcfc)
                bottomNavigationBackgroundMode(BottomNavBgMode.PRIMARY)
                bottomNavigationIconTextMode(BottomNavIconTextMode.SELECTED_ACCENT)
                swipeRefreshLayoutColorsRes(R.color.red)

                snackbarBackgroundColorDefault()
                snackbarTextColorDefault()
            }
        }

        NotificationHelper().enableBootReceiver(this)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}