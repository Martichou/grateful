package me.martichou.be.grateful.fragments.dialogFragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.add_bottomsheet_fragment.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.AddBottomsheetFragmentBinding
import me.martichou.be.grateful.utilities.*
import me.martichou.be.grateful.viewmodels.AddViewModel
import java.io.File

open class BottomsheetFragment : BottomSheetDialogFragment() {

    private val placePicker = 548

    private lateinit var binding: AddBottomsheetFragmentBinding
    private lateinit var viewModel: AddViewModel

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(this, InjectorUtils.provideAddViewModelFactory(context!!)).get(AddViewModel::class.java)
        binding = AddBottomsheetFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@BottomsheetFragment)
        }

        binding.hdl = this
        binding.addTitleNoteBs.requestFocus()

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
                        CompressImage(requireContext(), viewModel, image, add_photo_btn_bs)
                    }
                    CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                        makeToast(context!!, "We're sorry, there was an error.")
                    }
                }
            }
            placePicker -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        viewModel.place = PlacePicker.getPlace(context, data).address.toString()
                        add_loc_btn_bs.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_roundaccent)
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
            val titleOfTheNote: String = add_title_note_bs.text.toString()
            if (!titleOfTheNote.isEmpty()) run {
                viewModel.insertNote(Notes(titleOfTheNote, add_content_note_bs.text.toString(), viewModel.photoOrNot(), "", currentTime().toString(), viewModel.locOrNot()))
                dismiss()
            } else {
                makeToast(context!!, "Enter at least a title")
            }
        } else {
            makeToast(context!!, "Try again in a few seconds...")
        }
    }

}