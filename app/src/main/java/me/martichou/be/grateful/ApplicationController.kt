package me.martichou.be.grateful

import android.app.Activity
import android.app.Application
import android.app.Service
import androidx.preference.PreferenceManager
import com.mapbox.mapboxsdk.Mapbox
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.martichou.be.grateful.di.AppInjector
import me.martichou.be.grateful.util.notifications.NotificationHelper
import timber.log.Timber
import javax.inject.Inject

class ApplicationController : Application(), HasActivityInjector, HasServiceInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    @ExperimentalCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Mapbox.getInstance(this, getString(R.string.mapbox_apikey))
        AppInjector.init(this)

        // Check notification
        checkForNotification()
        NotificationHelper().enableBootReceiver(this)
    }

    /**
     * Check if it's needed to enable notification
     */
    private fun checkForNotification() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dailynotification", true)) {
            if (!NotificationHelper().checkIfExist(this)) {
                NotificationHelper().scheduleRepeatingRTCNotification(this,
                        PreferenceManager.getDefaultSharedPreferences(this).getInt("dn_hour", 20),
                        PreferenceManager.getDefaultSharedPreferences(this).getInt("dn_min", 0))
            }
        }
    }

    override fun activityInjector() = dispatchingAndroidInjector

    override fun serviceInjector() = dispatchingServiceInjector
}