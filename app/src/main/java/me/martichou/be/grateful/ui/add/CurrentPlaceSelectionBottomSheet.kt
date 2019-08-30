package me.martichou.be.grateful.ui.add

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.common.utils.GeocodingUtils.removeNameFromAddress
import me.martichou.be.grateful.R

class CurrentPlaceSelectionBottomSheet @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : CoordinatorLayout(context, attrs, defStyleAttr) {

    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var rootView: CoordinatorLayout? = null
    private var placeNameTextView: TextView? = null
    private var placeAddressTextView: TextView? = null
    private var placeProgressBar: ProgressBar? = null

    val isShowing: Boolean
        get() = bottomSheetBehavior!!.state != STATE_HIDDEN

    init {
        initialize(context)
    }

    private fun initialize(context: Context) {
        rootView = View.inflate(context, R.layout.custom_mapbox_view_bottom_sheet_container, this) as CoordinatorLayout
        bottomSheetBehavior = BottomSheetBehavior.from(rootView!!.findViewById<LinearLayout>(R.id.root_bottom_sheet))
        bottomSheetBehavior!!.isHideable = true
        bottomSheetBehavior!!.state = STATE_HIDDEN
        bindViews()
    }

    private fun bindViews() {
        placeNameTextView = findViewById(R.id.text_view_place_name)
        placeAddressTextView = findViewById(R.id.text_view_place_address)
        placeProgressBar = findViewById(R.id.progress_bar_place)
    }

    fun setPlaceDetails(@Nullable carmenFeature: CarmenFeature?) {
        if (!isShowing) {
            toggleBottomSheet()
        }
        if (carmenFeature == null) {
            placeNameTextView!!.text = ""
            placeAddressTextView!!.text = ""
            placeProgressBar!!.visibility = View.VISIBLE
            return
        }
        placeProgressBar!!.visibility = View.INVISIBLE

        placeNameTextView!!.text = if (carmenFeature.text() == null) "Dropped Pin" else carmenFeature.text()
        placeAddressTextView!!.text = removeNameFromAddress(carmenFeature)
    }

    fun dismissPlaceDetails() {
        toggleBottomSheet()
    }

    private fun toggleBottomSheet() {
        bottomSheetBehavior!!.peekHeight = rootView!!.findViewById<View>(R.id.bottom_sheet_header).height
        bottomSheetBehavior!!.isHideable = isShowing
        bottomSheetBehavior!!.state = if (isShowing) STATE_HIDDEN else STATE_COLLAPSED
    }
}