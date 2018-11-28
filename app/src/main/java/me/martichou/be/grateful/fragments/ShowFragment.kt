package me.martichou.be.grateful.fragments

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.ShowFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.viewmodels.ShowViewModel

class ShowFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: ShowViewModel
    private lateinit var binding: ShowFragmentBinding
    private lateinit var mMapView: MapView

    private var noteId: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noteId = ShowFragmentArgs.fromBundle(arguments).noteId
        viewModel = ViewModelProviders.of(this, InjectorUtils.provideShowViewModelFactory(requireContext().applicationContext, noteId)).get(ShowViewModel::class.java)
        binding = ShowFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@ShowFragment)
            this.hdl = this@ShowFragment
            this.showModel = viewModel
            executePendingBindings()
        }

        setHasOptionsMenu(true)

        mMapView = binding.map
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = p0.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context, R.raw.style_json))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        p0.uiSettings.setAllGesturesEnabled(false)
        p0.moveCamera(CameraUpdateFactory.newLatLng(LatLng(-34.0, 151.0)))
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.show, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_note -> {
                true
            }
            R.id.delete_note -> {

                LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.HORIZONTAL)
                        .setTopColorRes(R.color.red)
                        .setButtonsColorRes(R.color.black)
                        .setIcon(R.drawable.ic_delete_white)
                        .setTitle("Take a second...")
                        .setMessage("Are you sure you really want to delete this note? After that you won't be able to recover it.")
                        .setPositiveButton(android.R.string.yes) {
                            this.findNavController().popBackStack()
                            viewModel.deleteNote(noteId)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private class GeocoderHandler : Handler() {
            override fun handleMessage(message: Message) {
                val locationAddress: String? = when (message.what) {
                    1 -> {
                        val bundle = message.data
                        bundle.getString("address")
                    }
                    else -> null
                }
            }
        }
    }

}
