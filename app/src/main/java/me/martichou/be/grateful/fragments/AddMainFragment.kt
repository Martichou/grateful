package me.martichou.be.grateful.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import me.martichou.be.grateful.R
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.FragmentAddmainBinding
import me.martichou.be.grateful.utilities.CompressImage
import me.martichou.be.grateful.utilities.currentTime
import me.martichou.be.grateful.utilities.dateToSearch
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.imageCropper
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.viewmodels.AddViewModel
import java.io.File
import java.io.IOException

open class AddMainFragment : BottomSheetDialogFragment() {

    private val placePicker = 548

    private val viewModel by lazy {
        getViewModel { AddViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: FragmentAddmainBinding

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddmainBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@AddMainFragment)
            hdl = this@AddMainFragment
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    /**
     * Open the image selector
     */
    fun openImageSelector(v: View) {
        imageCropper(context!!, this)
    }

    /**
     * Open the place selector
     */
    fun openMapsSelector(v: View) {
        val builder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(activity), placePicker)
    }

    /**
     * Callback for the Matisse call
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val image = File(CropImage.getActivityResult(data).uri.path)
                        CompressImage(requireContext(), viewModel, image, binding.addPhotoBtnBs)
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                        makeToast(context!!, "We're sorry, there was an error.")
                    }
                }
            }
            placePicker -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val selected: String?
                        val place = PlacePicker.getPlace(context, data)
                        selected = try {
                            Geocoder(context).getFromLocation(place.latLng.latitude, place.latLng.longitude, 1)[0].locality
                        } catch (e: IOException) {
                            place.name.toString()
                        }
                        viewModel.placeCity = selected
                        binding.addLocBtnBs.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_roundaccent)
                    }
                    Activity.RESULT_CANCELED -> {
                        makeToast(context!!, "We're sorry, there was an error.")
                    }
                }
            }
        }
    }

    /**
     * Close this fragment and save info
     */
    // TODO - Check for color
    fun btnSaveAction(v: View) {
        if (!viewModel.isWorking) {
            val titleOfTheNote: String = binding.addTitleNoteBs.text.toString()
            if (!titleOfTheNote.isEmpty()) run {
                viewModel.insertNote(Notes(titleOfTheNote,
                        binding.addContentNoteBs.text.toString(),
                        viewModel.photoOrNot(),
                        "",
                        currentTime(),
                        dateToSearch(),
                        viewModel.locOrNot()))
                dismiss()
            } else {
                makeToast(context!!, "Enter at least a title")
            }
        } else {
            makeToast(context!!, "Try again in a few seconds...")
        }
    }

}