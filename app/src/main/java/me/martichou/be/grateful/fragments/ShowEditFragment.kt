package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import me.martichou.be.grateful.databinding.FragmentShoweditBinding
import me.martichou.be.grateful.viewmodels.EditViewModel
import me.martichou.be.grateful.viewmodels.getNotesRepository
import me.martichou.be.grateful.viewmodels.getViewModel

class ShowEditFragment : Fragment() {

    private val params by navArgs<ShowEditFragmentArgs>()
    private val viewModel by lazy {
        getViewModel { EditViewModel(getNotesRepository(requireContext()), params.noteId) }
    }
    private lateinit var binding: FragmentShoweditBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShoweditBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Bind databinding val
        binding.lifecycleOwner = viewLifecycleOwner
        binding.editModel = viewModel
        binding.hdl = this
    }

    /**
     * Delete the note button handler
     */
    fun deletethisnote(view: View) {
        AlertDialog.Builder(requireContext())
                .setTitle("Delete this gratitude?")
                .setMessage("This action can't be undone, think twice before hitting 'Yes'")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteNote()
                    findNavController().navigate(ShowEditFragmentDirections.actionEditFragmentToMainFragment())
                }.setNegativeButton("No") {
                    dialog, _ -> dialog.dismiss()
                }.show()
    }

    /**
     * Save the note edited button handler
     */
    fun editthisnote(view: View) {
        if (!binding.editnoteTitle.text.isNullOrEmpty()) {
            viewModel.updateNote(binding.editnoteTitle.text.toString(), binding.editnoteContent.text.toString())
            findNavController().popBackStack()
        } else {
            Snackbar.make(binding.root, "The title can't be null", Snackbar.LENGTH_SHORT)
        }
    }

}