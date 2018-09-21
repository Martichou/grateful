package me.martichou.be.grateful.fragments

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.add_fragment.*
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.AddFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.compressImage
import me.martichou.be.grateful.utilities.currentTime
import me.martichou.be.grateful.utilities.imageCropper
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.viewmodels.AddViewModel
import java.io.File

class AddFragment : Fragment() {

    private lateinit var viewModel: AddViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Define binding
        val binding = AddFragmentBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        // Use InjectorUtils to inject the viewmodel
        val factory = InjectorUtils.provideAddViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(AddViewModel::class.java)

        // Use this to bind onClick or other data binding from add_fragment.xml
        binding.hdl = this

        // Return the view
        return binding.root
    }

    /**
     * Close this fragment and switch back to the previous one
     */
    fun btnCloseAction(v: View) {
        v.findNavController().popBackStack()
    }

    /**
     * Open the image selector
     */
    fun openImageSelector(v: View) {
        imageCropper(context!!, this)
    }

    /**
     * Callback for the Matisse call
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val image = File(CropImage.getActivityResult(data).uri.path)
                compressImage(activity, true, viewModel, null, image, add_photo_btn, null)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                makeToast(context!!, "We're sorry, there was an error.")
            }
        }
    }

    @SuppressLint("ShowToast")
    /**
     * Close this fragment and save info
     */
    fun btnSaveAction(v: View) {
        if (!viewModel.isWorking) {
            val title: String = add_title_edit.text.toString()
            if (!title.isEmpty()) run {
                viewModel.insertNote(Notes(title, add_content_edit.text.toString(), viewModel.photoOrNot(), currentTime()))
                v.findNavController().popBackStack()
            } else {
                makeToast(context!!, "Enter at least a title")
            }
        } else {
            makeToast(context!!, "Try again in a few seconds...")
        }
    }
}