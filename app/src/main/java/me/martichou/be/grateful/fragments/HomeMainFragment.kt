package me.martichou.be.grateful.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.recyclerView.NotesAdapter
import me.martichou.be.grateful.utilities.AnimUtils
import me.martichou.be.grateful.utilities.DividerRV
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.statusBarWhite
import me.martichou.be.grateful.viewmodels.MainViewModel

class HomeMainFragment : Fragment() {

    private val viewModel by lazy {
        getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: FragmentHomemainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomemainBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@HomeMainFragment)
            thisVm = viewModel
            hdl = this@HomeMainFragment
        }

        // Prepare recyclerview
        val adapter = NotesAdapter()
        binding.recentNotesList.setHasFixedSize(true)
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

        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fab.visibility == View.VISIBLE) {
                    binding.fab.hide()
                } else if (dy < 0 && binding.fab.visibility != View.VISIBLE) {
                    binding.fab.show()
                }
            }
        })

        return binding.root
    }

    /**
     * Observe recentNotesList for and update adapter
     */
    private fun subscribeUirecentNotesList(adapter: NotesAdapter) {
        viewModel.getNotes().observe(viewLifecycleOwner, Observer { notes ->
            if (notes.isNullOrEmpty()) {
                binding.recentNotesList.visibility = View.GONE
                binding.nonethinking.visibility = View.VISIBLE
            } else {
                adapter.submitList(notes)
                binding.recentNotesList.visibility = View.VISIBLE
                binding.nonethinking.visibility = View.GONE
            }
        })
    }

    /**
     * New note action
     */
    fun btnNewAction(view: View) {
        val bottomsheetFragment = AddMainFragment()
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
    }

    fun gototest(view: View) {
    }

    private fun setupTransition() {
        exitTransition = Fade().apply {
            interpolator = AnimUtils.getFastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_small).toLong()
        }
    }
}