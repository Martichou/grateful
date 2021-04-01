package me.martichou.be.grateful.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.common.PlaceConstants
import com.mapbox.mapboxsdk.plugins.places.picker.viewmodel.PlacePickerViewModel
import me.martichou.be.grateful.R
import me.martichou.be.grateful.util.statusBarTrans
import timber.log.Timber
import java.util.*

class AddLocalizationActivity : AppCompatActivity(), OnMapReadyCallback, MapboxMap.OnCameraMoveStartedListener, MapboxMap.OnCameraIdleListener, Observer<CarmenFeature> {

    private var bottomSheet: CurrentPlaceSelectionBottomSheet? = null
    private var carmenFeature: CarmenFeature? = null
    private var viewModel: PlacePickerViewModel? = null
    private var markerImage: AppCompatImageView? = null
    private var mapboxMap: MapboxMap? = null
    private var accessToken: String? = null
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        val actionBar = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_addlocalizationactivity)

        statusBarTrans(this)
        if (savedInstanceState == null) {
            accessToken = getString(R.string.mapbox_apikey)
        }

        // Initialize the view model.
        viewModel = ViewModelProvider(this).get(PlacePickerViewModel::class.java)
        viewModel!!.results.observe(this, this)

        bindViews()
        addChosenLocationButton()

        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)
    }

    private fun bindViews() {
        mapView = findViewById(R.id.map_view)
        bottomSheet = findViewById(R.id.mapbox_plugins_picker_bottom_sheet)
        markerImage = findViewById(R.id.mapbox_plugins_image_view_marker)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.DARK) {
            // Initialize with the markers current location information.
            makeReverseGeocodingSearch()

            this@AddLocalizationActivity.mapboxMap!!.addOnCameraMoveStartedListener(this@AddLocalizationActivity)
            this@AddLocalizationActivity.mapboxMap!!.addOnCameraIdleListener(this@AddLocalizationActivity)
        }
    }

    override fun onCameraMoveStarted(reason: Int) {
        Timber.v("Map camera has begun moving.")
        if (markerImage!!.translationY == 0f) {
            markerImage!!.animate().translationY(-75f)
                    .setInterpolator(OvershootInterpolator()).setDuration(250).start()
            if (bottomSheet!!.isShowing) {
                bottomSheet!!.dismissPlaceDetails()
            }
        }
    }

    override fun onCameraIdle() {
        Timber.v("Map camera is now idling.")
        markerImage!!.animate().translationY(0f)
                .setInterpolator(OvershootInterpolator()).setDuration(250).start()
        bottomSheet!!.setPlaceDetails(null)
        makeReverseGeocodingSearch()
    }

    override fun onChanged(carmenFeatur: CarmenFeature?) {
        var carmenFeature = carmenFeatur
        if (carmenFeature == null) {
            carmenFeature = CarmenFeature.builder().placeName(
                    String.format(Locale.US, "[%f, %f]",
                            mapboxMap!!.cameraPosition.target.latitude,
                            mapboxMap!!.cameraPosition.target.longitude)
            ).text("No address found").properties(JsonObject()).build()
        }
        this.carmenFeature = carmenFeature
        bottomSheet!!.setPlaceDetails(carmenFeature)
    }

    private fun makeReverseGeocodingSearch() {
        val latLng = mapboxMap!!.cameraPosition.target
        viewModel!!.reverseGeocode(Point.fromLngLat(latLng.longitude, latLng.latitude), accessToken, null)
    }

    private fun addChosenLocationButton() {
        val placeSelectedButton = findViewById<FloatingActionButton>(R.id.place_chosen_button)
        val placeCloseButton = findViewById<FloatingActionButton>(R.id.place_chosen_close)
        placeSelectedButton.setOnClickListener {
            if (carmenFeature == null) {
                Snackbar.make(bottomSheet!!,
                        getString(R.string.mapbox_plugins_place_picker_not_valid_selection),
                        Snackbar.LENGTH_LONG).show()
            } else {
                placeSelected()
            }
        }
        placeCloseButton.setOnClickListener {
            finish()
        }
    }

    private fun placeSelected() {
        val json = carmenFeature!!.toJson()
        val returningIntent = Intent()
        returningIntent.putExtra(PlaceConstants.RETURNING_CARMEN_FEATURE, json)
        returningIntent.putExtra(PlaceConstants.MAP_CAMERA_POSITION, mapboxMap!!.cameraPosition)
        setResult(RESULT_OK, returningIntent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }
}