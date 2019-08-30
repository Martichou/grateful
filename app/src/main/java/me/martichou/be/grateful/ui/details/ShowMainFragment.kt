package me.martichou.be.grateful.ui.details

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.TransitionInflater
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.android.synthetic.main.fragment_showmain.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentShowmainBinding
import me.martichou.be.grateful.di.Injectable
import me.martichou.be.grateful.util.GlideApp
import me.martichou.be.grateful.util.statusBarTrans
import me.martichou.be.grateful.util.statusBarWhite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ShowMainFragment : Fragment(), Injectable, OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var showViewModel: ShowViewModel
    private val params by navArgs<ShowMainFragmentArgs>()
    private lateinit var binding: FragmentShowmainBinding
    private var handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireContext(), resources.getString(R.string.mapbox_apikey))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShowmainBinding.inflate(inflater, container, false)

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.move).apply {
            duration = 300
            interpolator = FastOutSlowInInterpolator()
        }

        binding.requestListener = imageListener

        // Set animation transition
        setupTransition()

        // Animation Watchdog - Make sure we don't wait longer than a second for the Glide image
        handler.postDelayed(1000) {
            startPostponedEnterTransition()
        }
        postponeEnterTransition()

        return binding.root
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        mapboxMap.setStyle(Style.DARK) {

            if (!showViewModel.note.value!!.location.isEmpty()) {

                Timber.d("Searching for ${showViewModel.note.value!!.location}")

                val mapboxGeocoding = MapboxGeocoding.builder()
                        .accessToken(Mapbox.getAccessToken()!!)
                        .query(showViewModel.note.value!!.location)
                        .build()

                mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse> {
                    override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                        val results = response.body()!!.features()
                        if (results.size > 0) {
                            // Log the first results Point.
                            val firstResultPoint = results[0].center()!!.coordinates()
                            Timber.d("onResponse: $firstResultPoint")

                            val position = CameraPosition.Builder()
                                    .target(LatLng(firstResultPoint[1], firstResultPoint[0]))
                                    .zoom(13.0)
                                    .build()

                            mapboxMap.animateCamera(CameraUpdateFactory
                                    .newCameraPosition(position), 7000)
                        } else {
                            Timber.d("onResponse: No result found")
                        }
                    }

                    override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                })
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showViewModel = ViewModelProvider(this, viewModelFactory).get(ShowViewModel::class.java).also {
            it.setNote(params.noteId)
        }
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.showModel = showViewModel
        binding.args = params
        binding.hdl = this

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
    }

    /**
     * Set statusbar as translucent
     */
    override fun onResume() {
        Timber.d("onResume called")
        statusBarTrans(activity)
        binding.mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        Timber.d("onPause called")
        statusBarWhite(activity)
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    /**
     * Setup fade out transition and sharedelementransition
     */
    private fun setupTransition() {
        enterTransition = Slide().apply { startDelay = 250 }
        returnTransition = Fade().apply {
            interpolator = FastOutLinearInInterpolator()
            duration = 75
        }
        exitTransition = Fade().apply { duration = 200 }
    }

    /**
     * Go to edit page button handler
     */
    fun editaction(view: View) {
        findNavController().navigate(ShowMainFragmentDirections.actionShowFragmentToEditFragment(params.noteId))
    }

    /**
     * Call animation once photo is loaded
     */
    private val imageListener = object : RequestListener<Drawable> {
        override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            return false
        }

        override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
        ): Boolean {
            startPostponedEnterTransition()
            return false
        }
    }

    fun openImageOfNote(view: View) {
        StfalconImageViewer.Builder(context, mutableListOf(showViewModel.note.value!!)) { vieww, image ->
            GlideApp.with(requireContext())
                    .load(File(context?.getDir("imgForNotes", Context.MODE_PRIVATE), image.image))
                    .into(vieww)
        }.withHiddenStatusBar(false)
                .show()
    }

}
