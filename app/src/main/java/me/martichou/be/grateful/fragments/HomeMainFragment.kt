package me.martichou.be.grateful.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import com.google.android.material.appbar.AppBarLayout
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.recyclerView.NotesAdapter
import me.martichou.be.grateful.utilities.AnimUtils
import me.martichou.be.grateful.utilities.DividerRV
import me.martichou.be.grateful.utilities.formatDate
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.viewmodels.MainViewModel

class HomeMainFragment : Fragment() {

    private val viewModel by lazy {
        getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: FragmentHomemainBinding
    private var isExpanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomemainBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@HomeMainFragment)
            thisVm = viewModel
            hdl = this@HomeMainFragment
        }

        // Prepare recyclerview and bind
        val adapter = NotesAdapter()
        binding.recentNotesList.layoutManager = LinearLayoutManager(context)
        binding.recentNotesList.setHasFixedSize(true)
        binding.recentNotesList.addItemDecoration(DividerRV())
        binding.recentNotesList.adapter = adapter
        subscribeUirecentNotesList(adapter)

        // Wait RecyclerView layout for detail to list image return animation
        postponeEnterTransition()
        binding.recentNotesList.doOnLayout {
            startPostponedEnterTransition()
        }

        // Setup exit animation with a fadeout
        setupTransition()

        // Hide fab on scroll and bottomappbar too
        setupScrollListener()

        // Arrow rotation on offset change
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val progress = (-i).toFloat() / totalScrollRange
            binding.datePickerArrow.rotation = 180 + progress * 180
            isExpanded = i == 0
        })

        // Update subtitle while scrolling
        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemPosition = (binding.recentNotesList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                if(binding.dateselected.text != formatDate(viewModel.getNotes().value!![itemPosition].dateToSearch))
                    binding.dateselected.text = formatDate(viewModel.getNotes().value!![itemPosition].dateToSearch)
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

    fun ooccalendar(view: View) {
        isExpanded = !isExpanded
        binding.appBar.setExpanded(isExpanded, true)
    }

    private fun setupTransition() {
        exitTransition = Fade().apply {
            interpolator = AnimUtils.getFastOutSlowInInterpolator()
            duration = resources.getInteger(R.integer.config_duration_area_small).toLong()
        }
    }

    private fun setupScrollListener(){
        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding.fab.visibility == View.VISIBLE) {
                    binding.fab.hide()
                    binding.bottomAppBar.animate().alpha(0.0f).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            binding.bottomAppBar.visibility = View.GONE
                        }

                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            binding.shadow.visibility = View.GONE
                        }
                    }).duration = 200
                } else if (dy < 0 && binding.fab.visibility != View.VISIBLE) {
                    binding.bottomAppBar.animate().alpha(1.0f).setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            binding.bottomAppBar.visibility = View.VISIBLE
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            binding.shadow.visibility = View.VISIBLE
                        }
                    }).duration = 200
                    binding.fab.show()
                }
            }
        })
    }
}