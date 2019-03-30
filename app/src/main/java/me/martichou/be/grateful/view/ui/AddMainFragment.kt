package me.martichou.be.grateful.view.ui

import android.app.DatePickerDialog
import android.content.DialogInterface
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
import me.martichou.be.grateful.utils.CompressImage
import me.martichou.be.grateful.viewmodel.AddViewModel
import me.martichou.be.grateful.viewmodel.getNotesRepository
import me.martichou.be.grateful.viewmodel.getViewModel
import java.io.File
import java.util.*

open class AddMainFragment : BottomSheetDialogFragment() {

    private val placePicker = 548

    private val viewModel by lazy {
        getViewModel { AddViewModel(getNotesRepository(requireContext()), activity!!.applicationContext) }
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(!viewModel.hasBeenSaved && viewModel.hasPhoto){
            viewModel.deleteImage()
        }
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
     * Open the place selector
     */
    fun openDateSelector(v: View) {
        val calendar = Calendar.getInstance()
        val c = DatePickerDialog(requireContext(), R.style.DialogTheme, DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day)
            viewModel.dateSelected = cal
            binding.addDateBtnBs.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_roundaccent)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        c.datePicker.maxDate = calendar.timeInMillis
        c.show()
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
                viewModel.hasBeenSaved = true
                viewModel.insertNote(
                    Notes(
                        titleOfTheNote,
                        binding.addContentNoteBs.text.toString(),
                        viewModel.randomImageName,
                        viewModel.dateDefaultOrNot(),
                        viewModel.dateOrNot(),
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