package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import me.martichou.be.grateful.databinding.FragmentShoweditBinding
import me.martichou.be.grateful.viewmodels.getNotesRepository
import me.martichou.be.grateful.viewmodels.getViewModel
import me.martichou.be.grateful.utilities.statusBarWhite
import me.martichou.be.grateful.viewmodels.EditViewModel

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
     * Set the status bar as white
     */
    override fun onResume() {
        super.onResume()
        statusBarWhite(activity)
    }

    /**
     * Delete the note button handler
     */
    fun deletethisnote(view: View) {
        viewModel.deleteNote()
        findNavController().navigate(ShowEditFragmentDirections.actionEditFragmentToMainFragment())
    }

    /**
     * Save the note edited button handler
     */
    fun editthisnote(view: View) {
        viewModel.updateNote(binding.editnoteTitle.text.toString(), binding.editnoteContent.text.toString())
        findNavController().popBackStack()
    }

}