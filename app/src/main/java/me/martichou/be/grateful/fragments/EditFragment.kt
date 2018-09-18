package me.martichou.be.grateful.fragments

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.edit_fragment.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.EditFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.compressImage
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.utilities.runOnIoThread
import me.martichou.be.grateful.viewmodels.EditViewModel
import java.io.File

class EditFragment : Fragment() {

    private var noteId: Long = 0
    private lateinit var editModel: EditViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        noteId = EditFragmentArgs.fromBundle(arguments).noteId

        // Use InjectorUtils to inject the viewmodel
        val factory = InjectorUtils.provideEditViewModelFactory(requireActivity(), noteId)
        editModel = ViewModelProviders.of(this, factory).get(EditViewModel::class.java)
        val binding =
            DataBindingUtil.inflate<EditFragmentBinding>(inflater, R.layout.edit_fragment, container, false).apply {
                viewModel = editModel
                setLifecycleOwner(this@EditFragment)
            }

        // Use this to bind onClick or other data binding from edit_fragment.xml
        binding.hdl = this

        // Return the view
        return binding.root
    }

    /**
     * This will open the image choser
     * and update the image.
     */
    fun btnEditImage(v: View){
        TedBottomPicker.Builder(this.requireContext())
            .setOnImageSelectedListener { it: Uri? ->
                val file = File(it!!.path)
                compressImage(activity, false, null, editModel, file, null, show_image_note)
            }
            .setEmptySelectionText("Cancel")
            .showGalleryTile(false) // Prevent user from picking image from google-photos, ... which seems not supported yet
            .create().show(fragmentManager)
    }

    /**
     * Save the edit and switch back to previous
     */
    fun btnDeleteAction(v: View) {
        // TODO - Ask for confirmation
        runOnIoThread {
            editModel.deleteNote(editModel.getThisNoteStatic(noteId))
        }
        v.findNavController().popBackStack(R.id.main_fragment, false)
    }

    /**
     * Save the edit and switch back to previous
     */
    fun btnCloseAction(v: View) {
        v.findNavController().popBackStack()
    }

    /**
     * Save the edit and switch back to previous
     */
    fun btnSaveAction(v: View) {
        runOnIoThread {
            val pn = editModel.getThisNoteStatic(noteId)
            val title = edit_title_note.text.toString()
            val content = edit_content_note.text.toString()

            if((!title.isEmpty() && title != pn.title) || (!content.isEmpty() && content != pn.content) || (editModel.hasPhotoUpdated)) {
                editModel.updateOnDb(pn, title, content, noteId)
                v.findNavController().popBackStack()
            } else {
                activity!!.runOnUiThread { makeToast(requireContext(), "Please, either hit back or edit something.") }
            }
        }
    }
}