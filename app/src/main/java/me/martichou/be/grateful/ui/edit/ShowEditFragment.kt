package me.martichou.be.grateful.ui.edit

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentShoweditBinding
import me.martichou.be.grateful.di.Injectable
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class ShowEditFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var editViewModel: EditViewModel
    private val params by navArgs<ShowEditFragmentArgs>()
    private lateinit var binding: FragmentShoweditBinding
    private lateinit var unregistrar: Unregistrar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShoweditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        editViewModel = ViewModelProvider(this, viewModelFactory).get(EditViewModel::class.java).also {
            it.setNote(params.noteId)
        }
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.editModel = editViewModel
        binding.hdl = this

        setupListener()
    }

    override fun onPause() {
        unregistrar.unregister()
        super.onPause()
    }

    @SuppressLint("RestrictedApi")
    private fun setupListener() {
        binding.editnoteTitle.addTextChangedListener {
            Timber.d("Edittitle changed")
            if (binding.cvSaving.visibility == View.INVISIBLE
                    && binding.editnoteTitle.text.toString() != editViewModel.note.value?.title) {
                binding.cvSaving.visibility = View.VISIBLE
            } else if (binding.cvSaving.visibility == View.VISIBLE
                    && binding.editnoteTitle.text.toString() == editViewModel.note.value?.title
                    && binding.editnoteContent.text.toString() == editViewModel.note.value?.content) {
                binding.cvSaving.visibility = View.INVISIBLE
            }
        }
        binding.editnoteContent.addTextChangedListener {
            Timber.d("Editcontent changed")
            if (binding.cvSaving.visibility == View.INVISIBLE
                    && binding.editnoteContent.text.toString() != editViewModel.note.value?.content) {
                binding.cvSaving.visibility = View.VISIBLE
            } else if (binding.cvSaving.visibility == View.VISIBLE
                    && binding.editnoteContent.text.toString() == editViewModel.note.value?.content
                    && binding.editnoteTitle.text.toString() == editViewModel.note.value?.title) {
                binding.cvSaving.visibility = View.INVISIBLE
            }
        }
        unregistrar = KeyboardVisibilityEvent.registerEventListener(activity) {
            Timber.d("IsKeyboardOpened $it")
            if (it) {
                binding.editDelete.visibility = View.GONE
            } else {
                binding.editDelete.visibility = View.VISIBLE
            }
        }
    }

    fun back(v: View) {
        findNavController().popBackStack()
    }

    /**
     * Delete the note button handler
     */
    fun deletethisnote(view: View) {
        AlertDialog.Builder(requireContext())
                .setTitle(resources.getString(R.string.delete_this))
                .setMessage(resources.getString(R.string.cant_be_undone))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val imageFile = File(context!!.getDir("imgForNotes", Context.MODE_PRIVATE), editViewModel.note.value!!.image)
                        if (imageFile.exists()) {
                            val deleted = imageFile.delete()
                            Timber.d("Deleting")
                            if (!deleted) {
                                Timber.e("Cannot delete the file..")
                            } else {
                                Timber.d("Succes. The file has been deleted")
                            }
                        }
                        editViewModel.deleteNote()
                    }
                    findNavController().navigate(ShowEditFragmentDirections.actionEditFragmentToMainFragment())
                }.setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }.show()
    }

    /**
     * Save the note edited button handler
     */
    fun editthisnote(view: View) {
        if (!binding.editnoteTitle.text.isNullOrEmpty()) {
            editViewModel.updateNote(binding.editnoteTitle.text.toString(), binding.editnoteContent.text.toString())
            findNavController().popBackStack()
        } else {
            Timber.d("Happen")
            Snackbar.make(binding.root, resources.getString(R.string.enter_title), Snackbar.LENGTH_SHORT).show()
        }
    }

}