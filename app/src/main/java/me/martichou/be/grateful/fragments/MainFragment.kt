package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.martichou.be.grateful.R
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.databinding.MainFragmentBinding
import me.martichou.be.grateful.fragments.dialogFragment.BottomsheetFragment
import me.martichou.be.grateful.utilities.DividerRV
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.roundProfile
import me.martichou.be.grateful.viewmodels.MainViewModel

class MainFragment : Fragment() {

    private val viewModel by lazy {
        getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: MainFragmentBinding
    private lateinit var notesList: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MainFragmentBinding.inflate(inflater, container, false).apply {
            hdl = this@MainFragment
            mainModel = viewModel
            setLifecycleOwner(this@MainFragment)
        }

        postponeEnterTransition()

        // Make image on main page rounded without a cardview
        roundProfile(binding.mainProfile)
        binding.mainLanding.clipToOutline = true
        binding.mainMapview.clipToOutline = true

        // Initialize the recyclerview
        notesList = binding.notesList
        notesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        notesList.addItemDecoration(DividerRV(40))
        notesList.setHasFixedSize(true)
        notesList.adapter = viewModel.adapter

        // Observe change
        subscribeUi(viewModel.adapter)

        notesList.doOnLayout {
            startPostponedEnterTransition()
        }

        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.sharedimage_exit)

        return binding.root
    }

    private fun subscribeUi(adapter: NotesAdapter) {
        viewModel.notesList.observe(viewLifecycleOwner, Observer { notes ->
            if (notes != null) {
                adapter.submitList(notes)
            }
        })
    }

    /**
     * Open the bottomsheet
     */
    fun btnNewAction(view: View) {
        Log.d("TGHSJK", viewModel.today.toString() + "okkkkkk")
        if (!(viewModel.today)) {
            val bottomsheetFragment = BottomsheetFragment()
            bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
        } else {
            // Open today note
        }
    }
}