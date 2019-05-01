package me.martichou.be.grateful.util

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.common.PlaceConstants
import me.martichou.be.grateful.ui.add.AddLocalizationActivity


internal object PlacePicker {

    @Nullable
    fun getPlace(data: Intent?): CarmenFeature? {
        val json = data?.getStringExtra(PlaceConstants.RETURNING_CARMEN_FEATURE) ?: return null
        return CarmenFeature.fromJson(json)
    }

    class IntentBuilder {

        private val intent: Intent = Intent()

        fun accessToken(@NonNull accessToken: String): IntentBuilder {
            intent.putExtra(PlaceConstants.ACCESS_TOKEN, accessToken)
            return this
        }

        fun build(activity: Activity): Intent {
            intent.setClass(activity, AddLocalizationActivity::class.java)
            return intent
        }
    }
}