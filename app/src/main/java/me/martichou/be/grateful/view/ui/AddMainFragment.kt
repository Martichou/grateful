package me.martichou.be.grateful.view.ui

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
import com.theartofdev.edmodo.cropper.CropImageView
import me.martichou.be.grateful.R
import me.martichou.be.grateful.data.model.Notes
import me.martichou.be.grateful.databinding.FragmentAddmainBinding
import me.martichou.be.grateful.utils.*
import me.martichou.be.grateful.viewmodel.AddViewModel
import me.martichou.be.grateful.viewmodel.getNotesRepository
import me.martichou.be.grateful.viewmodel.getViewModel
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
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(4, 4)
                .setAllowRotation(true)
                .start(requireContext(), this)
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
                    Toast.makeText(context, resources.getString(R.string.set_image), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(context, resources.getString(R.string.cant_get_place), Toast.LENGTH_SHORT).show()
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
                                dateDefault(), // TODO CONDITIONAL
                                dateDefault(), // TODO SAVE DATE AS A DD/MM/YYYY
                                viewModel.locOrNot()
                        )
                )
                dismiss()
            } else {
                Toast.makeText(context, resources.getString(R.string.enter_title), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, resources.getString(R.string.set_image), Toast.LENGTH_SHORT).show()
        }
    }
}