package me.martichou.be.grateful.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.transition.Explode
import com.google.android.material.appbar.AppBarLayout
import me.martichou.be.grateful.R
import me.martichou.be.grateful.databinding.FragmentHomemainBinding
import me.martichou.be.grateful.recyclerView.NotesAdapter
import me.martichou.be.grateful.utilities.DividerRV
import me.martichou.be.grateful.utilities.formatDate
import me.martichou.be.grateful.utilities.getNotesRepository
import me.martichou.be.grateful.utilities.getViewModel
import me.martichou.be.grateful.utilities.makeToast
import me.martichou.be.grateful.utilities.statusBarWhite
import me.martichou.be.grateful.utilities.stringToDate
import me.martichou.be.grateful.viewmodels.MainViewModel
import timber.log.Timber

class HomeMainFragment : Fragment() {

    private val viewModel by lazy {
        getViewModel { MainViewModel(getNotesRepository(requireContext())) }
    }
    private lateinit var binding: FragmentHomemainBinding
    private var isExpanded = false
    private var liststate: Parcelable? = null
    private val adapter = NotesAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomemainBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@HomeMainFragment
            thisVm = viewModel
            hdl = this@HomeMainFragment
        }

        // Prepare recyclerview and bind
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

        // Hide fab on scroll
        setupScrollListener()

        // Arrow rotation on offset change
        setupOffsetListener()

        // Update subtitle while scrolling
        setupScrollRvListener()

        return binding.root
    }

    // Temp workaround
    override fun onResume() {
        super.onResume()
        // Set back statusbar to white and dark icon
        statusBarWhite(activity)
        if (liststate != null) {
            Timber.d("Called onResume")
            // TODO - Fix sharedelement transition due to something idk
            // TODO - If the note wasn't showed in the first screen (when user hasn't scrolled yet)
            // TODO - then the sharedelementanimation is glitched.
            binding.recentNotesList.layoutManager?.onRestoreInstanceState(liststate)
        }
    }

    // Temp workaround
    override fun onPause() {
        super.onPause()
        liststate = binding.recentNotesList.layoutManager?.onSaveInstanceState()
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
                binding.recentNotesList.smoothScrollToPosition(0)
                if (binding.recentNotesList.visibility == View.GONE) {
                    binding.recentNotesList.visibility = View.VISIBLE
                    binding.nonethinking.visibility = View.GONE
                }
            }
        })
    }

    @SuppressLint("WrongConstant")
    private fun setupTransition() {
        exitTransition = Explode().apply {
            excludeTarget(binding.appBar, true)
            excludeTarget(binding.nonethinking, true)
            duration = 200.toLong()
        }
    }

    private fun setupScrollListener() {
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
    }

    private fun setupOffsetListener() {
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
            val totalScrollRange = appBarLayout.totalScrollRange
            val progress = (-i).toFloat() / totalScrollRange
            binding.datePickerArrow.rotation = 180 + progress * 180
            isExpanded = i == 0
        })
    }

    private fun setupScrollRvListener() {
        binding.recentNotesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemPosition = (binding.recentNotesList.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)

                try {
                    if (binding.dateselected.text != formatDate(viewModel.getNotes().value!![itemPosition[0]].dateToSearch)) {

                        val inAnim = AnimationUtils.loadAnimation(context, R.anim.slide_up)
                        val outAnim = AnimationUtils.loadAnimation(context, R.anim.slide_down)

                        binding.dateselected.startAnimation(outAnim)

                        binding.dateselected.text = formatDate(viewModel.getNotes().value!![itemPosition[0]].dateToSearch)

                        binding.dateselected.startAnimation(inAnim)

                        binding.compactcalendarView.date = stringToDate(viewModel.getNotes().value!![itemPosition[0]].dateToSearch)!!.time
                    }
                } catch (e: IndexOutOfBoundsException) {
                    Timber.d("FATAL ERROR, INDEX OUT OF BOUND $e")
                }
            }
        })
    }

    /**
     * Action button
     */
    fun btnNewAction(view: View) {
        val bottomsheetFragment = AddMainFragment()
        bottomsheetFragment.show(fragmentManager, bottomsheetFragment.tag)
    }

    fun ooccalendar(view: View) {
        isExpanded = !isExpanded
        binding.appBar.setExpanded(isExpanded, true)
    }

    fun gototop(view: View) {
        val item = (binding.recentNotesList.layoutManager as StaggeredGridLayoutManager).findFirstCompletelyVisibleItemPositions(null)[0]
        Timber.d("Item $item")
        when (item) {
            0 -> makeToast(requireContext(), "Already showing today's gratitude")
            -1 -> makeToast(requireContext(), "Add a gratitude first !")
            else -> binding.recentNotesList.smoothScrollToPosition(0)
        }
    }
}