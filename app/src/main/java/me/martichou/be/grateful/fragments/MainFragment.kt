package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.Fade
import me.martichou.be.grateful.R
import me.martichou.be.grateful.adapters.NotesAdapter
import me.martichou.be.grateful.databinding.MainFragmentBinding
import me.martichou.be.grateful.fragments.dialogFragment.BottomsheetFragment
import me.martichou.be.grateful.utilities.*
import me.martichou.be.grateful.viewmodels.MainViewModel

class MainFragment : Fragment() {

    private val viewModel by lazy {
        getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = MainFragmentBinding.inflate(inflater, container, false).apply {
            hdl = this@MainFragment
            setLifecycleOwner(this@MainFragment)
        }

        // Prepare recyclerview
        val adapter = NotesAdapter()
        binding.recentNotesList.addItemDecoration(DividerRV())
        binding.recentNotesList.adapter = adapter
        subscribeUirecentNotesList(adapter)

        // Wait RecyclerView to layout for detail to list image return animation
        postponeEnterTransition()
        binding.recentNotesList.doOnLayout {
            startPostponedEnterTransition()
            statusBarWhite(requireActivity())
        }
        setupTransition()

        return binding.root
    }

    /**
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        viewModel.getNotes().observe(viewLifecycleOwner, Observer { notes ->
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

    private fun setupTransition() {
        exitTransition = Fade().apply {
            interpolator = AnimUtils.getFastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_small).toLong()
        }
    }
}