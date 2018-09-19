package me.martichou.be.grateful.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import kotlinx.android.synthetic.main.edit_fragment.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.EditFragmentBinding
import me.martichou.be.grateful.utilities.Glide4Engine
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.compressImage
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.utilities.runOnIoThread
import me.martichou.be.grateful.viewmodels.EditViewModel
import java.io.File

class EditFragment : Fragment(){

    private val codeCh = 234
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
    fun btnEditImage(v: View) {
        Matisse.from(this@EditFragment)
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
        if (requestCode == codeCh && resultCode == RESULT_OK) {
            val image = File(Matisse.obtainPathResult(data!!)[0])
            compressImage(activity, false, null, editModel, image, null, show_image_note)
        }
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

            if ((!title.isEmpty() && title != pn.title) || (!content.isEmpty() && content != pn.content) || (editModel.hasPhotoUpdated)) {
                editModel.updateOnDb(pn, title, content, noteId)
                v.findNavController().popBackStack()
            } else {
                activity!!.runOnUiThread { makeToast(requireContext(), "Please, either hit back or edit something.") }
            }
        }
    }
}