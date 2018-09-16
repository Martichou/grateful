package me.martichou.be.grateful.fragments

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.edit_fragment.*
import me.martichou.be.grateful.R
import me.martichou.be.grateful.data.Notes
import me.martichou.be.grateful.databinding.EditFragmentBinding
import me.martichou.be.grateful.utilities.InjectorUtils
import me.martichou.be.grateful.utilities.runOnIoThread
import me.martichou.be.grateful.viewmodels.EditViewModel

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

        // Use this to bind onClick or other data binding from main_fragment.xml
        binding.hdl = this

        // Return the view
        return binding.root
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

    @SuppressLint("ShowToast")
        /**
     * Save the edit and switch back to previous
     */
    fun btnSaveAction(v: View) {
        // TODO
        runOnIoThread {
            val pn = editModel.getThisNoteStatic(noteId)
            val title = edit_title_note.text.toString()
            val content = edit_content_note.text.toString()

            if((!title.isEmpty() && title != pn.title)
                || (!content.isEmpty() && content != pn.content)) {
                saveOnDb(pn, title, content)
            } else if (editModel.hasPhotoUpdated){
                this.findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Please, either hit back or edit something.", Toast.LENGTH_SHORT)
            }
        }
    }

    /**
     * Save the note on the db if it's needed
     * updateNote is on runOnIoThread
     */
    fun saveOnDb(pn: Notes, title: String, content: String){
        val n = Notes(title, content, pn.image, pn.date)
        n.id = noteId

        editModel.updateNote(n)
        this.findNavController().popBackStack()
    }
}