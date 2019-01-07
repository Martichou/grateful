package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.R
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.databinding.MainFragmentBinding
import me.martichou.be.grateful.fragments.dialogFragment.BottomsheetFragment
import me.martichou.be.grateful.utilities.DividerRV
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.statusBarWhite
import me.martichou.be.grateful.viewmodels.MainViewModel

class MainFragment : Fragment() {

    private val viewModel by lazy {
        getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: MainFragmentBinding

    private lateinit var recentNotesList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MainFragmentBinding.inflate(inflater, container, false).apply {
            hdl = this@MainFragment
            setLifecycleOwner(this@MainFragment)
        }

        postponeEnterTransition()

        statusBarWhite(requireActivity())

        // Prepare recyclerview
        recentNotesList = binding.recentNotesList
        recentNotesList.addItemDecoration(DividerRV())
        recentNotesList.adapter = viewModel.adapterRecently

        // Observe change
        subscribeUirecentNotesList(viewModel.adapterRecently)

        recentNotesList.doOnLayout {
            startPostponedEnterTransition()
        }

        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.sharedimage_exit)

        return binding.root
    }

    /**
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        viewModel.recentNotesList.observe(viewLifecycleOwner, Observer { notes ->
            if (notes != null) {
                adapter.submitList(notes)
            }
        })
    }

    /**
     * New note action
     */
    fun btnNewAction(view: View) {
        val bottomsheetFragment = BottomsheetFragment()
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
    }
}