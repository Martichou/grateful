package me.martichou.be.grateful

import android.app.Activity
import android.app.Application
import com.mapbox.mapboxsdk.Mapbox
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import me.martichou.be.grateful.di.AppInjector
import timber.log.Timber
import javax.inject.Inject

class ApplicationController : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Mapbox.getInstance(this, getString(R.string.mapbox_apikey))
        AppInjector.init(this)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}