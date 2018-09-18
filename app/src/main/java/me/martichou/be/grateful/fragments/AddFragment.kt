package me.martichou.be.grateful.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.add_fragment.*
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.AddFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.compressImage
import me.martichou.be.grateful.utilities.currentTime
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
        TedBottomPicker.Builder(this.requireContext())
            .setOnImageSelectedListener {

                val file = File(it.path)
                viewModel.changeIsWorking(true)

                compressImage(activity, true, viewModel, null, file, add_photo_btn, null)
            }
            .setEmptySelectionText("Cancel")
            .showGalleryTile(false) // Prevent user from picking image from google-photos, ... which seems not supported yet
            .create().show(fragmentManager)
    }

    /**
     * Return the photo name if there is one
     * else, blank
     */
    private fun photoOrNot(): String {
        return if (viewModel.hasPhoto) {
            viewModel.randomImageName
        } else {
            ""
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
                viewModel.insertNote(Notes(title, add_content_edit.text.toString(), photoOrNot(), currentTime()))
                v.findNavController().popBackStack()
            } else {
                makeToast(context!!, "Enter at least one title.")
            }
        } else {
            makeToast(context!!, "Try again in a few seconds...")
        }
    }
}