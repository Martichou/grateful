package me.martichou.be.grateful.fragments

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import me.martichou.be.grateful.utilities.imageCropper
import me.martichou.be.grateful.viewmodels.AddViewModel
import me.martichou.be.grateful.viewmodels.getNotesRepository
import me.martichou.be.grateful.viewmodels.getViewModel
import java.io.File

open class AddMainFragment : BottomSheetDialogFragment() {

    private val placePicker = 548

    private val viewModel by lazy {
        getViewModel { AddViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: FragmentAddmainBinding

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddmainBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@AddMainFragment
            hdl = this@AddMainFragment
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): BottomSheetDialog = BottomSheetDialog(requireContext(), theme)

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
                if (resultCode == AppCompatActivity.RESULT_OK)
                    CompressImage(requireContext(), viewModel, File(CropImage.getActivityResult(data).uri.path), binding.addPhotoBtnBs)
                else {
                    Toast.makeText(context, "Please select an image", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            placePicker -> {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    val place = PlacePicker.getPlace(context, data)
                    val namePlace = Geocoder(context).getFromLocation(place.latLng.latitude, place.latLng.longitude, 1)
                    if (!namePlace.isNullOrEmpty())
                        viewModel.placeCity = namePlace[0].locality
                    else {
                        Toast.makeText(context, "Can't get place name", Toast.LENGTH_SHORT).show()
                        return
                    }

                    binding.addLocBtnBs.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_roundaccent)
                }
            }
        }
    }

    /**
     * Close this fragment and save info
     */
    fun btnSaveAction(v: View) {
        if (!viewModel.isWorking && viewModel.hasPhoto) {
            val titleOfTheNote: String = binding.addTitleNoteBs.text.toString()
            if (!titleOfTheNote.isEmpty()) run {
                viewModel.insertNote(
                        Notes(
                                titleOfTheNote,
                                binding.addContentNoteBs.text.toString(),
                                viewModel.randomImageName,
                                currentTime(),
                                dateToSearch(),
                                viewModel.locOrNot()
                        )
                )
                dismiss()
            } else {
                Toast.makeText(context, "Enter at least a title", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "You have to set an image", Toast.LENGTH_SHORT).show()
        }
    }
}