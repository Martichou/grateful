package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import me.martichou.be.grateful.R
import me.martichou.be.grateful.R.id.main_fragment
import me.martichou.be.grateful.databinding.FragmentShoweditBinding
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.statusBarWhite
import me.martichou.be.grateful.viewmodels.EditViewModel

class ShowEditFragment : Fragment() {

    private var noteId: Long = 0
    private val viewModel by lazy {
        getViewModel { EditViewModel(getNotesRepository(requireContext()), noteId) }
    }
    private lateinit var binding: FragmentShoweditBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noteId = ShowMainFragmentArgs.fromBundle(arguments!!).noteId
        binding = FragmentShoweditBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@ShowEditFragment)
            editModel = viewModel
            hdl = this@ShowEditFragment
        }

        statusBarWhite(activity)

        return binding.root
    }

    fun deletethisnote(view: View) {
        viewModel.deleteNote()
        findNavController().navigate(ShowEditFragmentDirections.actionEditFragmentToMainFragment())
    }

    fun editthisnote(view: View) {
        viewModel.updateNote(binding.editnoteTitle.text.toString(), binding.editnoteContent.text.toString())
        findNavController().popBackStack()
    }

    fun launchpickimage(view: View) {
        // TODO
    }
}