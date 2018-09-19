package me.martichou.be.grateful.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.add_fragment.*
import kotlinx.android.synthetic.main.edit_fragment.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.AddFragmentBinding
import me.martichou.be.grateful.utilities.Glide4Engine
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.compressImage
import me.martichou.be.grateful.utilities.currentTime
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.viewmodels.AddViewModel
import java.io.File

class AddFragment : Fragment() {

    private val codeCh = 234
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
        Matisse.from(this@AddFragment)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(1)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.85f)
            .theme(R.style.Matisse_Dracula)
            .imageEngine(Glide4Engine())
            .forResult(codeCh)
    }

    /**
     * Callback for the Matisse call
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == codeCh && resultCode == Activity.RESULT_OK) {
            val image = File(Matisse.obtainPathResult(data!!)[0])
            compressImage(activity, true, viewModel, null, image, add_photo_btn, null)
        }
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